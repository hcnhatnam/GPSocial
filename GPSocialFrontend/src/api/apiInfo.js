import request from '@/utils/request'
import axios from 'axios'

export function getOtherPeoPle() {
  return request({
    url: '/people',
    method: 'get'
  })
}
export function getIp() {
  return axios.get(
    "http://bot.whatismyipaddress.com"
  )
}
export function getInfoWithIp2location(ip) {
  const params = ip
  return request({
    url: '/ip2location',
    method: 'get',
    params
  })
}
