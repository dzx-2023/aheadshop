import request from './request'

/** 每日签到 */
export function checkIn() {
  return request.post('/user/sign/check-in')
}

/** 补签指定日期 (yyyy-MM-dd) */
export function retroSign(date: string) {
  return request.post(`/user/sign/retro/${date}`)
}

/** 当月签到状态 */
export function getSignStatus(year?: number, month?: number) {
  return request.get('/user/sign/status', { params: { year, month } })
}

/** 连续签到天数 */
export function getStreak() {
  return request.get('/user/sign/streak')
}

/** 我的积分信息 */
export function getPoints() {
  return request.get('/user/sign/points')
}

/** 积积分流水(分页) */
export function getPointsLog(pageNum = 1, pageSize = 10) {
  return request.get('/user/sign/log', { params: { pageNum, pageSize } })
}

/** 积分排行榜 */
export function getLeaderboard(type: 'total' | 'month' = 'total') {
  return request.get('/user/sign/leaderboard', { params: { type } })
}
