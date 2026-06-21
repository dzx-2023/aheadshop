/** 签到结果 */
export interface SignResultVO {
  /** 本次获得积分(基础+奖励) */
  points: number
  /** 当前连续签到天数 */
  streak: number
  /** 连续奖励积分 */
  bonusPoints: number
  /** 总积分 */
  totalPoints: number
  /** 当月已签到日期列表 */
  signDays: number[]
}

/** 签到状态(日历) */
export interface SignStatusVO {
  year: number
  month: number
  /** 当月已签到的日期列表(1~31) */
  signDays: number[]
  /** 本月签到总天数 */
  totalDays: number
  /** 本月已补签次数 */
  retroUsed: number
  /** 本月补签上限 */
  retroLimit: number
  /** 今日是否已签到 */
  todaySigned: boolean
}

/** 积分信息 */
export interface PointsVO {
  totalPoints: number
  monthPoints: number
  rank: number
}

/** 排行榜项 */
export interface LeaderboardItemVO {
  rank: number
  userId: number
  nickname: string
  avatar: string
  points: number
}

/** 积分流水项 */
export interface PointsLogVO {
  id: number
  points: number
  type: number
  typeName: string
  description: string
  createTime: string
}

/** 积分流水类型 */
export const PointsLogType = {
  SIGN: 1,
  STREAK_BONUS: 2,
  RETRO_COST: 3,
  EXCHANGE: 4,
  SYSTEM: 5,
} as const

export const PointsLogTypeText: Record<number, string> = {
  1: '每日签到',
  2: '连续奖励',
  3: '补签消耗',
  4: '积分兑换',
  5: '系统赠送',
}
