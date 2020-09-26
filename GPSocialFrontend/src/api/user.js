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
