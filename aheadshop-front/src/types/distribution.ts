/** 分销中心信息 */
export interface DistributionInfo {
  userId: number
  inviteCode: string
  level: number
  totalCommission: number
  availableCommission: number
  frozenCommission: number
  totalTeamCount: number
  status: number
}

/** 推荐人信息 */
export interface ReferrerInfo {
  userId: number
  nickname: string
  avatar: string | null
  inviteCode: string
  bindTime: string
}

/** 佣金汇总 */
export interface CommissionSummary {
  totalCommission: number
  availableCommission: number
  frozenCommission: number
}

/** 佣金记录 */
export interface CommissionRecord {
  id: number
  distributorId: number
  orderNo: string
  buyerId: number
  orderAmount: number
  commissionRate: number
  commissionAmount: number
  status: number
  settleTime: string | null
  createTime: string
}

/** 团队成员 */
export interface TeamMember {
  userId: number
  nickname: string
  avatar: string | null
  joinTime: string
}

/** 提现记录 */
export interface WithdrawalRecord {
  id: number
  distributorId: number
  amount: number
  status: number
  auditTime: string | null
  payTime: string | null
  remark: string | null
  createTime: string
}

/** 佣金状态：0=冻结 1=已结算 2=已取消 */
export const CommissionStatusMap: Record<number, { label: string; type: string }> = {
  0: { label: '冻结中', type: 'warning' },
  1: { label: '已结算', type: 'success' },
  2: { label: '已取消', type: 'info' },
}

/** 提现状态：0=待审核 1=已通过 2=已打款 3=已拒绝 */
export const WithdrawalStatusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已打款', type: 'success' },
  3: { label: '已拒绝', type: 'danger' },
}
