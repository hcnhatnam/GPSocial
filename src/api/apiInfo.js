import request from '@/utils/request'

export function getOtherPeoPle() {
  return request({
    url: '/people',
    method: 'get'
  })
}
