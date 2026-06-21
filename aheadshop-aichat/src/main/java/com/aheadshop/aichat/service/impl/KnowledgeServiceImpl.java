package com.aheadshop.aichat.service.impl;

import com.aheadshop.aichat.domain.po.ChatFaq;
import com.aheadshop.aichat.domain.vo.ProductSummary;
import com.aheadshop.aichat.feign.ProductFeignClient;
import com.aheadshop.aichat.mapper.ChatFaqMapper;
import com.aheadshop.aichat.service.KnowledgeService;
import com.aheadshop.common.core.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RAG 知识检索服务实现
 *
 * <p>检索流程：
 * <ol>
 *   <li>匹配 FAQ 常见问题（数据库 + Redis 缓存）</li>
 *   <li>从用户消息中提取关键词</li>
 *   <li>用关键词调用商品服务搜索商品</li>
 *   <li>将检索到的信息格式化为文本注入 system prompt</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final ProductFeignClient productFeignClient;
    private final ChatFaqMapper chatFaqMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String FAQ_CACHE_KEY = "chat:faq:list";
    private static final long FAQ_CACHE_TTL_MINUTES = 10;

    /** 中文停用词（高频无意义词） */
    private static final Set<String> STOP_WORDS = Set.of(
            "的", "了", "吗", "呢", "吧", "啊", "哦", "嗯", "是", "在", "有",
            "和", "与", "或", "但", "而", "也", "都", "就", "才", "只", "又",
            "我", "你", "他", "她", "它", "我们", "你们", "他们",
            "这", "那", "这个", "那个", "什么", "怎么", "怎样", "如何",
            "哪", "哪里", "哪个", "多少", "几", "可以", "能", "会", "要",
            "想", "看看", "看", "买", "卖", "没有", "有没有",
            "请问", "请", "帮", "帮忙", "一下", "想问", "想问一下",
            "商品", "产品", "东西", "物品"
    );

    /** 分词用的标点符号正则（使用 unicode 转义避免中文引号与 Java 字符串定界符冲突） */
    private static final String PUNCTUATION_REGEX =
            "[，。！？、；:\u201C\u201D\u2018\u2019（）【】\\s,.!?;:()\\[\\]]+";

    @Override
    public String retrieve(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return "";
        }

        StringBuilder knowledge = new StringBuilder();

        // 1. FAQ 匹配（优先级最高，命中即返回）
        String faqAnswer = matchFaqFromDb(userMessage);
        if (faqAnswer != null) {
            knowledge.append("【常见问题参考】\n").append(faqAnswer);
            log.debug("FAQ 命中: message={}", userMessage);
            return knowledge.toString();
        }

        // 2. 商品知识检索
        List<ProductSummary> products = searchProducts(userMessage);
        if (!products.isEmpty()) {
            knowledge.append("【相关商品信息】\n");
            knowledge.append(buildKnowledgeContext(products));
            log.debug("商品检索命中: count={}, keywords={}", products.size(), extractKeywords(userMessage));
        }

        return knowledge.toString();
    }

    /**
     * 从数据库匹配 FAQ（带 Redis 缓存）
     */
    private String matchFaqFromDb(String message) {
        List<ChatFaq> faqList = loadFaqList();
        String lower = message.toLowerCase();

        // 按 sortOrder 降序匹配（权重高的优先）
        for (ChatFaq faq : faqList) {
            if (faq.getKeywords() == null || faq.getKeywords().isBlank()) continue;
            String[] keywords = faq.getKeywords().split(",");
            for (String keyword : keywords) {
                keyword = keyword.trim();
                if (!keyword.isEmpty() && lower.contains(keyword.toLowerCase())) {
                    return faq.getAnswer();
                }
            }
        }
        return null;
    }

    /**
     * 加载 FAQ 列表（Redis 缓存，10 分钟过期）
     */
    private List<ChatFaq> loadFaqList() {
        // 尝试从缓存读取
        try {
            String cached = redisTemplate.opsForValue().get(FAQ_CACHE_KEY);
            if (cached != null) {
                return parseFaqCache(cached);
            }
        } catch (Exception e) {
            log.warn("读取 FAQ 缓存失败", e);
        }

        // 从数据库加载
        List<ChatFaq> faqList = chatFaqMapper.selectList(
                new LambdaQueryWrapper<ChatFaq>()
                        .eq(ChatFaq::getStatus, 1)
                        .orderByDesc(ChatFaq::getSortOrder)
        );

        // 写入缓存
        try {
            StringBuilder sb = new StringBuilder();
            for (ChatFaq faq : faqList) {
                sb.append(faq.getId()).append("|")
                  .append(faq.getAnswer().replace("\n", "\\n")).append("|")
                  .append(faq.getKeywords() != null ? faq.getKeywords() : "").append("\n");
            }
            redisTemplate.opsForValue().set(FAQ_CACHE_KEY, sb.toString(), FAQ_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("写入 FAQ 缓存失败", e);
        }

        return faqList;
    }

    /**
     * 解析 FAQ 缓存字符串
     */
    private List<ChatFaq> parseFaqCache(String cached) {
        List<ChatFaq> list = new ArrayList<>();
        for (String line : cached.split("\n")) {
            String[] parts = line.split("\\|", 3);
            if (parts.length < 3) continue;
            ChatFaq faq = new ChatFaq();
            try { faq.setId(Long.parseLong(parts[0])); } catch (Exception ignored) {}
            faq.setAnswer(parts[1].replace("\\n", "\n"));
            faq.setKeywords(parts[2]);
            list.add(faq);
        }
        return list;
    }

    /**
     * 从用户消息中提取关键词
     */
    private List<String> extractKeywords(String message) {
        String[] tokens = message.split(PUNCTUATION_REGEX);

        List<String> keywords = new ArrayList<>();
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty() || token.length() < 2) continue;
            if (STOP_WORDS.contains(token)) continue;
            if (token.matches("\\d+")) continue;
            keywords.add(token);
        }

        // 提取英文品牌/型号（如 iPhone, iPad）
        Pattern engPattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = engPattern.matcher(message);
        while (matcher.find()) {
            String eng = matcher.group();
            if (!keywords.contains(eng.toLowerCase()) && eng.length() >= 2) {
                keywords.add(eng);
            }
        }

        if (keywords.size() > 5) {
            keywords = keywords.subList(0, 5);
        }

        return keywords;
    }

    /**
     * 用关键词搜索商品
     */
    private List<ProductSummary> searchProducts(String userMessage) {
        List<String> keywords = extractKeywords(userMessage);
        if (keywords.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> seenIds = new HashSet<>();
        List<ProductSummary> results = new ArrayList<>();

        for (String keyword : keywords) {
            if (results.size() >= 8) break;
            try {
                Result<?> result = productFeignClient.searchProducts(keyword, 1, 1, 5);
                if (result == null || result.getData() == null) continue;

                List<ProductSummary> parsed = parseProductResults(result.getData());
                for (ProductSummary p : parsed) {
                    if (seenIds.add(p.getId())) {
                        results.add(p);
                    }
                }
            } catch (Exception e) {
                log.warn("商品搜索异常: keyword={}", keyword, e);
            }
        }

        return results;
    }

    @SuppressWarnings("unchecked")
    private List<ProductSummary> parseProductResults(Object data) {
        List<ProductSummary> products = new ArrayList<>();
        try {
            if (data instanceof Map) {
                Map<String, Object> pageMap = (Map<String, Object>) data;
                Object recordsObj = pageMap.get("records");
                if (recordsObj instanceof List) {
                    List<Map<String, Object>> records = (List<Map<String, Object>>) recordsObj;
                    for (Map<String, Object> record : records) {
                        products.add(mapToProduct(record));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析商品数据异常", e);
        }
        return products;
    }

    private ProductSummary mapToProduct(Map<String, Object> map) {
        return ProductSummary.builder()
                .id(toLong(map.get("id")))
                .name(Objects.toString(map.get("name"), ""))
                .subtitle(Objects.toString(map.get("subtitle"), ""))
                .categoryName(Objects.toString(map.get("categoryName"), ""))
                .brandName(Objects.toString(map.get("brandName"), ""))
                .minPrice(map.get("minPrice") instanceof Number
                        ? ((Number) map.get("minPrice")).doubleValue() : null)
                .sales(toInt(map.get("sales")))
                .build();
    }

    /**
     * 构建商品知识上下文文本
     */
    private String buildKnowledgeContext(List<ProductSummary> products) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            ProductSummary p = products.get(i);
            sb.append(String.format("%d. %s", i + 1, p.getName()));
            if (p.getSubtitle() != null && !p.getSubtitle().isBlank()) {
                sb.append(" — ").append(p.getSubtitle());
            }
            sb.append("\n");
            if (p.getCategoryName() != null && !p.getCategoryName().isBlank()) {
                sb.append("   分类：").append(p.getCategoryName());
            }
            if (p.getBrandName() != null && !p.getBrandName().isBlank()) {
                sb.append("  品牌：").append(p.getBrandName());
            }
            sb.append("\n");
            if (p.getMinPrice() != null) {
                sb.append("   价格：¥").append(String.format("%.2f", p.getMinPrice()));
            }
            if (p.getSales() != null && p.getSales() > 0) {
                sb.append("  销量：").append(p.getSales()).append("件");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private Long toLong(Object obj) {
        if (obj instanceof Number) return ((Number) obj).longValue();
        try { return Long.parseLong(obj.toString()); } catch (Exception e) { return null; }
    }

    private Integer toInt(Object obj) {
        if (obj instanceof Number) return ((Number) obj).intValue();
        try { return Integer.parseInt(obj.toString()); } catch (Exception e) { return null; }
    }
}
