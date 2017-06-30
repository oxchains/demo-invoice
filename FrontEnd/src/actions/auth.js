import axios from 'axios';
import { browserHistory } from 'react-router';
import {
  ROOT_URL,
  AUTH_USER,
  UNAUTH_USER,
  AUTH_ERROR,
  FETCH_INVOICE
} from './types';


/**
 * 登录
 * @param username
 * @param password
 * @param biz           true:企业登录, false:个人登录
 * @returns {Function}
 */
export function signinAction({username, password, biz}) {
  return function(dispatch) {
    axios.post(`${ROOT_URL}/token`, { username, password, biz })
      .then(response => {
        if(response.data.status == 1) {//auth success
          // - Save the JWT token

          localStorage.setItem('token', response.data.data.token);
          //localStorage.setItem('user', JSON.stringify(response.data.data));
          localStorage.setItem('username', username);
          localStorage.setItem('biz', JSON.stringify(biz));

          dispatch({type: AUTH_USER});
          // - redirect to the route '/'
          browserHistory.push('/');
        } else {//auth fail
          dispatch(authError(response.data.message));
        }

      })
      .catch( (err) => dispatch(authError(err.message)) );
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
  localStorage.removeItem('biz');

  return { type: UNAUTH_USER };
}

/**
 * 注册
 * @param name
 * @param mobile
 * @param password
 * @param callback
 * @returns {Function}
 */
export function signupUser({ name, mobile, password }, callback) {
  //console.log(`signupUser: ${username}, ${mobile}, ${password}`);
  return function(dispatch) {
    axios.post(`${ROOT_URL}/user`, { name, mobile, password })
      .then(response => {

        if(response.data.status == 1) {//singup success
          callback();
        } else {//signup fail
          callback(response.data.message);
        }
      })
      .catch(err => callback(err.message));
  }
}


/**
 * 企业申请注册
 * @param values
 * @returns {Function}
 */
export function signupCompany(values, callback) {
  return function(dispatch) {
    axios.post(`${ROOT_URL}/company`, { ...values })
      .then(response => {
        if(response.data.status == 1) {//singup success
          callback();
        } else {//signup fail
          callback(response.data.message);
        }
      })
      .catch(err => callback(err.message));
  }
}