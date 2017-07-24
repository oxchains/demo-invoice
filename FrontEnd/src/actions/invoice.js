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
  FETCH_INVOICE_LIST,
  FETCH_INVOICE_DETAIL,
  SELECT_INVOICE,
  DESELECT_INVOICE,
  REIMBURSE_SUCCESS,
  getAuthorizedHeader,
  requestError
} from './types';


/**
 * 开票
 * @param values : {
 *   "target": "JD",
 *   "title": "xfja",
 *   "goods": [{
 *       "name": "computer",
 *       "description": "computer",
 *       "quantity": 5,
 *       "price": 4999
 *   }]
 * }
 * @returns {Function}
 */
export function addAction(values, callback) {
  return function(dispatch) {
    axios.post(`${ROOT_URL}/invoice`, { ...values }, { headers: getAuthorizedHeader() })
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

/**
 * 发票列表
 * @param page
 * @returns {Function}
 */
export function fetchInvoiceList(page) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/invoice`, { headers: getAuthorizedHeader() })
      .then(response => dispatch({ type: FETCH_INVOICE_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}


/**
 * 从列表中选择发票
 * @param serial
 * @returns {{type, payload: *}}
 */
export function selectInvoice(serial) {
  return {
    type: SELECT_INVOICE,
    payload: serial
  };
}

/**
 * 从列表中取消选择发票
 * @param serial
 * @returns {{type, payload: *}}
 */
export function deselectInvoice(serial) {
  return {
    type: DESELECT_INVOICE,
    payload: serial
  };
}

/**
 * 发票详情
 * @param serial
 * @returns {Function}
 */
export function fetchInvoiceDetail(serial) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/invoice/${serial}`, { headers: getAuthorizedHeader() })
      .then(response => dispatch({ type: FETCH_INVOICE_DETAIL, payload:response }))
      .catch( response => dispatch(requestError(response.data.error)) );
  }
}

/**
 * 报销发票
 * @param ids
 * @returns {Function}
 */
export function reimburseAction(ids, company, remark, callback) {
  return function(dispatch) {
    axios.post(`${ROOT_URL}/reimbursement?invoices=${ids.join()}&company=${company}&remark=${remark||''}`, null, { headers: getAuthorizedHeader() })
      .then(response => {
        if(response.data.status == 1) {// success
          dispatch({ type: REIMBURSE_SUCCESS, payload:response })
          callback();
        } else {//fail
          callback(response.data.message);
        }
      })
      .catch(err => callback(err.message));
  }
}


/**
 *
 * @returns {Function}
 */
export function clearReimburseResult() {
  return function(dispatch) {
    dispatch({ type: REIMBURSE_SUCCESS, payload:{ data:{data:null} } });
  }
}

/**
 * 发票流转
 * @param serial
 * @param target
 * @param biz           0:个人, 1:企业
 * @param callback
 * @returns {Function}
 */
export function transferAction({serial, target, biz}, callback) {
  return function(dispatch) {
    axios.put(`${ROOT_URL}/invoice?invoice=${serial}&target=${target}&biz=${biz?1:0}`, null, { headers: getAuthorizedHeader() })
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