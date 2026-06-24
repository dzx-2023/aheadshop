import request from './request'
import type { DistributionInfo, ReferrerInfo, CommissionSummary, CommissionRecord, TeamMember, WithdrawalRecord } from '@/types/distribution'
import type { PageResult } from '@/types/common'

/** 获取分销中心信息 */
export function getDistributionInfo() {
  return request.get<any, DistributionInfo>('/distribution/info')
}

/** 获取团队成员列表 */
export function getTeamList(params?: { pageNum?: number; pageSize?: number }) {
  return request.get<any, PageResult<TeamMember>>('/distribution/team', { params })
}

/** 获取我的推荐人信息 */
export function getReferrerInfo() {
  return request.get<any, ReferrerInfo | null>('/distribution/referrer')
}

/** 解除推荐关系 */
export function unbindReferral() {
  return request.post<any, void>('/distribution/referrer/unbind')
}

/** 获取佣金汇总 */
export function getCommissionSummary() {
  return request.get<any, CommissionSummary>('/distribution/commission/summary')
}

/** 佣金明细列表 */
export function getCommissionList(params?: { status?: number; pageNum?: number; pageSize?: number }) {
  return request.get<any, PageResult<CommissionRecord>>('/distribution/commission/list', { params })
}

/** 申请提现 */
export function applyWithdraw(data: { amount: number }) {
  return request.post<any, void>('/distribution/withdraw/apply', data)
}

/** 提现记录列表 */
export function getWithdrawList(params?: { pageNum?: number; pageSize?: number }) {
  return request.get<any, PageResult<WithdrawalRecord>>('/distribution/withdraw/list', { params })
}
