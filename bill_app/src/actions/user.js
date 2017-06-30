import axios from 'axios';
import {
  ROOT_URL,
  REQUEST_SUCCESS,
  REQUEST_ERROR,
  FETCH_USER_LIST,
  ADD_USER_SUCCESS,
  ADD_USER_ERROR
} from './types';

export function requestError(error) {
  return {
    type: REQUEST_ERROR,
    payload: error
  };
}

//节点列表
export function fetchUserList() {
  return function (dispatch) {
    axios.get(`${ROOT_URL}/user`)
      .then(response => dispatch({type: FETCH_USER_LIST, payload: response}))
      .catch(err => dispatch(requestError(err.message)));
  }
}

export function usersignUp({username, asset}, callback) {
  return function (dispatch) {
    axios.post(`${ROOT_URL}/user`, {"user": username, asset}).then(response => {
      if (response.data.status == 1) {
        console.log("response success");
        callback({isAddFail: false});
      } else {
        console.log("response fail");
        callback({isAddFail: true});
      }
    });
  }
}