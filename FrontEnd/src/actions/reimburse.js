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
  getAuthorizedHeader
} from './types';

export function requestError(error) {
  return {
    type: REQUEST_ERROR,
    payload: error
  };
}

/**
 * 报销列表
 * @param page
 * @returns {Function}
 */
export function fetchReimburseList(page) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/reimbursement`, { headers: getAuthorizedHeader() })
      .then(response => dispatch({ type: FETCH_REIMBURSE_LIST, payload:response }))
      .catch( response => dispatch(requestError(response.data.error)) );
  }
}

/**
 * 报销详情
 * @param serial
 * @returns {Function}
 */
export function fetchReimburse(serial) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/reimbursement/${serial}`, { headers: getAuthorizedHeader() })
      .then(response => dispatch({ type: FETCH_REIMBURSE, payload:response }))
      .catch( response => dispatch(requestError(response.data.error)) );
  }
}

/**
 * 确认/拒绝报销
 * @param id
 * @param action        0:拒绝, 1:确认
 * @param remark
 * @param callback
 * @returns {Function}
 */
export function reimburseAction(id, action, remark, callback) {
  return function(dispatch) {
    axios.put(`${ROOT_URL}/reimbursement?id=${id}&action=${action}`, null, { headers: getAuthorizedHeader() })
      .then(response => {
        if(response.data.status == 1) {// success
          callback();
        } else {//fail
          callback(response.data.message);
        }
      })
      .catch(err => callback(err.message));
  }
}