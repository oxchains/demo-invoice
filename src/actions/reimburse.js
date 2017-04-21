/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Email: iyakexi@gmail.com
 * Date: 20/04/2017
 *
 */

import axios from 'axios';
import { browserHistory } from 'react-router';
import {
  ROOT_URL,
  REQUEST_SUCCESS,
  REQUEST_ERROR,
  FETCH_REIMBURSE_LIST,
  FETCH_REIMBURSE,
  FETCH_MY_REIMBURSE_LIST
} from './types';

export function requestError(error) {
  return {
    type: REQUEST_ERROR,
    payload: error
  };
}

//企业报销列表
export function fetchReimburseList(page) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/reimburse-list/${page}`)
      .then(response => dispatch({ type: FETCH_REIMBURSE_LIST, payload:response }))
      .catch( response => dispatch(requestError(response.data.error)) );
  }
}

//我的报销列表
export function fetchMyReimburseList(page) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/reimburse-mine-list/${page}`)
      .then(response => dispatch({ type: FETCH_MY_REIMBURSE_LIST, payload:response }))
      .catch( response => dispatch(requestError(response.data.error)) );
  }
}

//报销详情
export function fetchReimburse(id) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/reimburse-${id}`)
      .then(response => dispatch({ type: FETCH_REIMBURSE, payload:response }))
      .catch( response => dispatch(requestError(response.data.error)) );
  }
}