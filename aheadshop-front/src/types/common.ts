/** 分页结果（后端 PageResult 对应） */
export interface PageResult<T> {
  total: number
  records: T[]
}
