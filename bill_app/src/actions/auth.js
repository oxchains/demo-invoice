import axios from 'axios';
import {
  ROOT_URL,
  AUTH_USER,
  UNAUTH_USER,
  AUTH_ERROR,
  SIGN_SUCCESS,
  ADD_USER_SUCCESS,
  ADD_USER_ERROR
} from './types';

// 登录
export function signinAction({username, password}, callback) {
  return function (dispatch) {

    // 暂时没有网络请求
    localStorage.setItem('username', username);

    dispatch({type: AUTH_USER, username});
    // - redirect to the route '/'
    callback();
  }
}

export function authError(error) {
  return {
    type: AUTH_ERROR,
    payload: error
  };
}

// 登出
export function signoutUser() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  localStorage.removeItem('username');

  return {type: UNAUTH_USER};
}
