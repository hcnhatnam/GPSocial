import request from '@/utils/request'

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
export function registerUser(email, username, profilePicLink, password) {
  return request({
    url: '/user/register',
    method: 'post',
    params: {
      email: email,
      username: username,
      profilepiclink: profilePicLink,
      password: password
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
export function getUserById(id) {
  return request({
    url: '/user/id',
    method: 'get',
    params: {
      id: id
    }
  })
}

export function saveUser(username, email, profilepiclink) {
  return request({
    url: '/user/id',
    method: 'put',
    params: {
      username: username,
      email: email,
      profilepiclink: profilepiclink
    }
  })
}

export function getUserByEmail(email) {
  return request({
    url: '/user/email',
    method: 'get',
    params: {
      email: email
    }
  })
}
