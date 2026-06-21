package com.aheadshop.user.service.impl;

import com.aheadshop.common.core.exception.BusinessException;
import com.aheadshop.common.core.exception.BusinessExceptionCode;
import com.aheadshop.common.core.result.PageResult;
import com.aheadshop.user.domain.po.PointsLog;
import com.aheadshop.user.domain.po.User;
import com.aheadshop.user.domain.po.UserPoints;
import com.aheadshop.user.domain.vo.*;
import com.aheadshop.user.mapper.PointsLogMapper;
import com.aheadshop.user.mapper.UserMapper;
import com.aheadshop.user.mapper.UserPointsMapper;
import com.aheadshop.user.service.ISignService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignServiceImpl implements ISignService {

    private final StringRedisTemplate redisTemplate;
    private final UserPointsMapper userPointsMapper;
    private final PointsLogMapper pointsLogMapper;
    private final UserMapper userMapper;

    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyyMM");

    // ── 积分规则 ──
    private static final int BASE_POINTS = 10;
    private static final int MAX_DAILY_POINTS = 60;
    private static final int RETRO_COST = 20;
    private static final int RETRO_LIMIT = 3;

    // ── 连续签到奖励阶梯 (连续天数 → 额外积分) ──
    private static final TreeMap<Integer, Integer> STREAK_BONUS = new TreeMap<>();
    static {
        STREAK_BONUS.put(3, 5);
        STREAK_BONUS.put(7, 10);
        STREAK_BONUS.put(14, 20);
        STREAK_BONUS.put(30, 50);
    }

    // ── 积分流水类型 ──
    public static final int TYPE_SIGN = 1;
    public static final int TYPE_STREAK_BONUS = 2;
    public static final int TYPE_RETRO_COST = 3;
    public static final int TYPE_EXCHANGE = 4;
    public static final int TYPE_SYSTEM = 5;

    // ── Redis Key 前缀 ──
    private static final String KEY_BITMAP_PREFIX = "sign:";
    private static final String KEY_STREAK_PREFIX = "sign:streak:";
    private static final String KEY_POINTS_TOTAL = "sign:points:total:";
    private static final String KEY_POINTS_MONTH = "sign:points:month:";
    private static final String KEY_RANK_TOTAL = "sign:rank:total";
    private static final String KEY_RANK_MONTH_PREFIX = "sign:rank:month:";
    private static final String KEY_RETRO_PREFIX = "sign:retro:";

    // ═══════════════════════════════════════════
    //  签到
    // ═══════════════════════════════════════════

    @Override
    @Transactional
    public SignResultVO checkIn(Long userId) {
        LocalDate today = LocalDate.now();
        String ym = today.format(YM_FMT);
        int dayOffset = today.getDayOfMonth() - 1;
        String bitmapKey = KEY_BITMAP_PREFIX + userId + ":" + ym;

        // 1. 检查是否已签到
        Boolean alreadySigned = redisTemplate.opsForValue().getBit(bitmapKey, dayOffset);
        if (Boolean.TRUE.equals(alreadySigned)) {
            throw new BusinessException(BusinessExceptionCode.SIGN_ALREADY_DONE, "今日已签到");
        }

        // 2. 执行签到 (SETBIT 返回旧值, 双重检查)
        Boolean old = redisTemplate.opsForValue().setBit(bitmapKey, dayOffset, true);
        if (Boolean.TRUE.equals(old)) {
            throw new BusinessException(BusinessExceptionCode.SIGN_ALREADY_DONE, "今日已签到");
        }

        // 3. 计算连续天数
        int streak = calculateStreak(userId, today);

        // 4. 更新连续天数缓存
        redisTemplate.opsForValue().set(KEY_STREAK_PREFIX + userId, String.valueOf(streak));

        // 5. 计算积分
        int bonusPoints = getStreakBonus(streak);
        int todayPoints = Math.min(BASE_POINTS + bonusPoints, MAX_DAILY_POINTS);

        // 6. 更新 Redis 积分 & 排行榜
        updateRedisPoints(userId, ym, todayPoints);

        // 7. 持久化到 MySQL
        persistPoints(userId, todayPoints, TYPE_SIGN, "每日签到", null);
        if (bonusPoints > 0) {
            persistPoints(userId, bonusPoints, TYPE_STREAK_BONUS,
                    "连续签到" + streak + "天奖励", null);
        }

        // 8. 获取当月签到日期列表
        List<Integer> signDays = getSignDays(userId, ym, today.lengthOfMonth());

        return SignResultVO.builder()
                .points(todayPoints)
                .streak(streak)
                .bonusPoints(bonusPoints)
                .totalPoints(getTotalPoints(userId))
                .signDays(signDays)
                .build();
    }

    // ═══════════════════════════════════════════
    //  补签
    // ═══════════════════════════════════════════

    @Override
    @Transactional
    public void retroSign(Long userId, String dateStr) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(dateStr);
        } catch (Exception e) {
            throw new BusinessException(BusinessExceptionCode.SIGN_RETRO_DATE_INVALID, "日期格式无效");
        }

        // 校验: 必须是本月、且在今天之前
        YearMonth currentMonth = YearMonth.from(today);
        if (!YearMonth.from(targetDate).equals(currentMonth)) {
            throw new BusinessException(BusinessExceptionCode.SIGN_RETRO_DATE_INVALID, "只能补签本月日期");
        }
        if (!targetDate.isBefore(today)) {
            throw new BusinessException(BusinessExceptionCode.SIGN_RETRO_DATE_INVALID, "只能补签过去的日期");
        }

        String ym = currentMonth.format(YM_FMT);
        int dayOffset = targetDate.getDayOfMonth() - 1;
        String bitmapKey = KEY_BITMAP_PREFIX + userId + ":" + ym;

        // 校验: 该日未签到
        if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(bitmapKey, dayOffset))) {
            throw new BusinessException(BusinessExceptionCode.SIGN_ALREADY_DONE, "该日已签到");
        }

        // 校验: 补签次数未超限
        String retroKey = KEY_RETRO_PREFIX + userId + ":" + ym;
        String retroCountStr = redisTemplate.opsForValue().get(retroKey);
        int retroCount = retroCountStr != null ? Integer.parseInt(retroCountStr) : 0;
        if (retroCount >= RETRO_LIMIT) {
            throw new BusinessException(BusinessExceptionCode.SIGN_RETRO_LIMIT, "本月补签次数已用完");
        }

        // 校验: 积分充足
        int totalPoints = getTotalPoints(userId);
        if (totalPoints < RETRO_COST) {
            throw new BusinessException(BusinessExceptionCode.SIGN_POINTS_NOT_ENOUGH, "积分不足，补签需要" + RETRO_COST + "积分");
        }

        // 执行补签
        redisTemplate.opsForValue().setBit(bitmapKey, dayOffset, true);
        redisTemplate.opsForValue().increment(retroKey);
        redisTemplate.expire(retroKey, getDaysUntilMonthEnd(today) + 1, TimeUnit.DAYS);

        // 扣减积分 (用负数记录消耗)
        updateRedisPoints(userId, ym, -RETRO_COST);
        persistPoints(userId, -RETRO_COST, TYPE_RETRO_COST,
                "补签 " + dateStr, dateStr);
    }

    // ═══════════════════════════════════════════
    //  查询: 当月签到状态
    // ═══════════════════════════════════════════

    @Override
    public SignStatusVO getMonthStatus(Long userId, Integer year, Integer month) {
        LocalDate today = LocalDate.now();
        if (year == null) year = today.getYear();
        if (month == null) month = today.getMonthValue();

        YearMonth ym = YearMonth.of(year, month);
        String ymStr = ym.format(YM_FMT);
        String bitmapKey = KEY_BITMAP_PREFIX + userId + ":" + ymStr;
        int daysInMonth = ym.lengthOfMonth();

        List<Integer> signDays = getSignDays(userId, ymStr, daysInMonth);

        // 补签次数
        String retroKey = KEY_RETRO_PREFIX + userId + ":" + ymStr;
        String retroCountStr = redisTemplate.opsForValue().get(retroKey);
        int retroUsed = retroCountStr != null ? Integer.parseInt(retroCountStr) : 0;

        // 今日是否已签到
        boolean todaySigned = false;
        if (YearMonth.from(today).equals(ym)) {
            int todayOffset = today.getDayOfMonth() - 1;
            todaySigned = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().getBit(bitmapKey, todayOffset));
        }

        return SignStatusVO.builder()
                .year(year)
                .month(month)
                .signDays(signDays)
                .totalDays(signDays.size())
                .retroUsed(retroUsed)
                .retroLimit(RETRO_LIMIT)
                .todaySigned(todaySigned)
                .build();
    }

    // ═══════════════════════════════════════════
    //  查询: 连续签到天数
    // ═══════════════════════════════════════════

    @Override
    public Integer getStreak(Long userId) {
        String cached = redisTemplate.opsForValue().get(KEY_STREAK_PREFIX + userId);
        if (cached != null) {
            return Integer.parseInt(cached);
        }
        // 缓存未命中, 重新计算
        int streak = calculateStreak(userId, LocalDate.now());
        redisTemplate.opsForValue().set(KEY_STREAK_PREFIX + userId, String.valueOf(streak));
        return streak;
    }

    // ═══════════════════════════════════════════
    //  查询: 我的积分
    // ═══════════════════════════════════════════

    @Override
    public PointsVO getPoints(Long userId) {
        int total = getTotalPoints(userId);
        int month = getMonthPoints(userId);

        // 总积分排名
        Long rank = redisTemplate.opsForZSet().reverseRank(KEY_RANK_TOTAL, String.valueOf(userId));
        long rankDisplay = rank != null ? rank + 1 : 0;

        return PointsVO.builder()
                .totalPoints(total)
                .monthPoints(month)
                .rank(rankDisplay)
                .build();
    }

    // ═══════════════════════════════════════════
    //  查询: 积分流水
    // ═══════════════════════════════════════════

    @Override
    public PageResult<PointsLogVO> getPointLog(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<PointsLog>()
                .eq(PointsLog::getUserId, userId)
                .orderByDesc(PointsLog::getCreateTime);

        Page<PointsLog> page = pointsLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<PointsLogVO> records = page.getRecords().stream()
                .map(log -> PointsLogVO.builder()
                        .id(log.getId())
                        .points(log.getPoints())
                        .type(log.getType())
                        .typeName(getTypeName(log.getType()))
                        .description(log.getDescription())
                        .createTime(log.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), records);
    }

    // ═══════════════════════════════════════════
    //  查询: 排行榜
    // ═══════════════════════════════════════════

    @Override
    public List<LeaderboardItemVO> getLeaderboard(String type) {
        String rankKey = "month".equalsIgnoreCase(type)
                ? KEY_RANK_MONTH_PREFIX + YearMonth.now().format(YM_FMT)
                : KEY_RANK_TOTAL;

        // 取 Top 30
        Set<ZSetOperations.TypedTuple<String>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(rankKey, 0, 29);

        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询用户信息
        List<Long> userIds = tuples.stream()
                .map(t -> Long.parseLong(Objects.requireNonNull(t.getValue())))
                .collect(Collectors.toList());

        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        }

        List<LeaderboardItemVO> result = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            Long uid = Long.parseLong(Objects.requireNonNull(tuple.getValue()));
            int points = tuple.getScore() != null ? tuple.getScore().intValue() : 0;
            User user = userMap.get(uid);
            result.add(LeaderboardItemVO.builder()
                    .rank(rank++)
                    .userId(uid)
                    .nickname(user != null ? user.getNickname() : "用户" + uid)
                    .avatar(user != null ? user.getAvatar() : null)
                    .points(points)
                    .build());
        }

        return result;
    }

    // ═══════════════════════════════════════════
    //  内部方法
    // ═══════════════════════════════════════════

    /**
     * 计算连续签到天数(从今天向前)
     */
    private int calculateStreak(Long userId, LocalDate today) {
        String ym = today.format(YM_FMT);
        String bitmapKey = KEY_BITMAP_PREFIX + userId + ":" + ym;
        int dayOffset = today.getDayOfMonth() - 1;

        int streak = 0;
        // 从今天向前遍历
        for (int i = dayOffset; i >= 0; i--) {
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(bitmapKey, i))) {
                streak++;
            } else {
                break;
            }
        }

        // 如果当月1号也签到了, 检查上月末尾
        if (streak > 0 && (dayOffset - streak + 1) == 0) {
            LocalDate prevMonth = today.minusMonths(1);
            String prevYm = prevMonth.format(YM_FMT);
            String prevKey = KEY_BITMAP_PREFIX + userId + ":" + prevYm;
            int prevLastDay = prevMonth.lengthOfMonth();
            for (int i = prevLastDay - 1; i >= 0; i--) {
                if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(prevKey, i))) {
                    streak++;
                } else {
                    break;
                }
            }
        }

        return streak;
    }

    /**
     * 获取连续签到对应的额外奖励积分
     */
    private int getStreakBonus(int streak) {
        // 找到 <= streak 的最大阶梯
        Map.Entry<Integer, Integer> entry = STREAK_BONUS.floorEntry(streak);
        return entry != null ? entry.getValue() : 0;
    }

    /**
     * 读取当月已签到的日期列表
     */
    private List<Integer> getSignDays(Long userId, String ym, int daysInMonth) {
        String bitmapKey = KEY_BITMAP_PREFIX + userId + ":" + ym;
        List<Integer> days = new ArrayList<>();
        for (int i = 0; i < daysInMonth; i++) {
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(bitmapKey, i))) {
                days.add(i + 1);
            }
        }
        return days;
    }

    /**
     * 更新 Redis 积分和排行榜
     */
    private void updateRedisPoints(Long userId, String ym, int points) {
        // 总积分
        String totalKey = KEY_POINTS_TOTAL + userId;
        redisTemplate.opsForValue().increment(totalKey, points);

        // 月积分
        String monthKey = KEY_POINTS_MONTH + userId + ":" + ym;
        redisTemplate.opsForValue().increment(monthKey, points);
        redisTemplate.expire(monthKey, getDaysUntilMonthEnd(LocalDate.now()) + 1, TimeUnit.DAYS);

        // 排行榜
        redisTemplate.opsForZSet().incrementScore(KEY_RANK_TOTAL, String.valueOf(userId), points);
        String rankMonthKey = KEY_RANK_MONTH_PREFIX + ym;
        redisTemplate.opsForZSet().incrementScore(rankMonthKey, String.valueOf(userId), points);
        redisTemplate.expire(rankMonthKey, getDaysUntilMonthEnd(LocalDate.now()) + 31, TimeUnit.DAYS);
    }

    /**
     * 持久化积分变动到 MySQL
     */
    private void persistPoints(Long userId, int points, int type, String desc, String bizNo) {
        // 写积分流水
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setPoints(points);
        log.setType(type);
        log.setDescription(desc);
        log.setBizNo(bizNo);
        pointsLogMapper.insert(log);

        // 更新积分账户 (upsert)
        UserPoints up = userPointsMapper.selectOne(
                new LambdaQueryWrapper<UserPoints>().eq(UserPoints::getUserId, userId));
        if (up == null) {
            up = new UserPoints();
            up.setUserId(userId);
            up.setTotalPoints(Math.max(points, 0));
            up.setMonthPoints(Math.max(points, 0));
            userPointsMapper.insert(up);
        } else {
            up.setTotalPoints(up.getTotalPoints() + points);
            up.setMonthPoints(up.getMonthPoints() + points);
            if (up.getTotalPoints() < 0) up.setTotalPoints(0);
            if (up.getMonthPoints() < 0) up.setMonthPoints(0);
            userPointsMapper.updateById(up);
        }
    }

    /**
     * 获取用户总积分 (优先从 Redis 读取)
     */
    private int getTotalPoints(Long userId) {
        String val = redisTemplate.opsForValue().get(KEY_POINTS_TOTAL + userId);
        if (val != null) return Integer.parseInt(val);
        // Redis 未命中, 从 DB 读取并回填
        UserPoints up = userPointsMapper.selectOne(
                new LambdaQueryWrapper<UserPoints>().eq(UserPoints::getUserId, userId));
        int total = up != null ? up.getTotalPoints() : 0;
        redisTemplate.opsForValue().set(KEY_POINTS_TOTAL + userId, String.valueOf(total));
        return total;
    }

    /**
     * 获取用户本月积分
     */
    private int getMonthPoints(Long userId) {
        String ym = YearMonth.now().format(YM_FMT);
        String monthKey = KEY_POINTS_MONTH + userId + ":" + ym;
        String val = redisTemplate.opsForValue().get(monthKey);
        if (val != null) return Integer.parseInt(val);
        // Redis 未命中, 从 DB 读取
        UserPoints up = userPointsMapper.selectOne(
                new LambdaQueryWrapper<UserPoints>().eq(UserPoints::getUserId, userId));
        return up != null ? up.getMonthPoints() : 0;
    }

    /**
     * 距离月底还有多少天
     */
    private long getDaysUntilMonthEnd(LocalDate date) {
        return YearMonth.from(date).lengthOfMonth() - date.getDayOfMonth();
    }

    /**
     * 积分类型编号 → 中文名
     */
    private String getTypeName(int type) {
        return switch (type) {
            case TYPE_SIGN -> "每日签到";
            case TYPE_STREAK_BONUS -> "连续奖励";
            case TYPE_RETRO_COST -> "补签消耗";
            case TYPE_EXCHANGE -> "积分兑换";
            case TYPE_SYSTEM -> "系统赠送";
            default -> "其他";
        };
    }
}
