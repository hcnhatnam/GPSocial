import request from '@/utils/request'
import {
  getInfoFromExtApi
} from "@/api/apiExt";

const tokens = {
  admin: {
    token: 'admin-token'
  },
  editor: {
    token: 'editor-token'
  }
}

const users = {
  'admin-token': {
    roles: ['admin'],
    introduction: 'I am a super administrator',
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    name: 'Super Admin'
  },
  'editor-token': {
    roles: ['editor'],
    introduction: 'I am an editor',
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
    name: 'Normal Editor'
  }
}

export async function login(user) {
  // const info = await getInfoFromExtApi();
  console.log("info", user);
  return request({
    url: '/user/login',
    method: 'post',
    params: {
      ...user
    }
  })
}

export function getInfo(token) {
  return request({
    url: '/user/info',
    method: 'get',
    params: {
      token
    }
  })
}

export function logout(email) {
  return request({
    url: '/user/logout',
    method: 'post',
    params: {
      email: email
    }
  })
}
