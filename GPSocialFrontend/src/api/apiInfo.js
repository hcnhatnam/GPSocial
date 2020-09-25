import request from '@/utils/request'
import axios from 'axios'

export function getOnlineUsers(email) {
  return request({
    url: '/user/onlineusers',
    method: 'get',
    params: {
      email: email
    }
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
