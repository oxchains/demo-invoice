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
  INVOICE_AUTO
} from './types';

//http请求错误
export function requestError(error) {
  return {
    type: REQUEST_ERROR,
    payload: error
  };
}

//自动开票
export function autoAction(values) {
  return function(dispatch) {
    //TODO: using GET for test only
    //axios.post(`${ROOT_URL}/auto`, { ...values })
    axios.get(`${ROOT_URL}/auto`, { ...values })
      .then(response => {
        if(response.data.status == 1) {// success
          dispatch({ type: REQUEST_SUCCESS, payload: response.data.message });
        } else {//fail
          dispatch(requestError(response.data.message));
        }
      })
      .catch( response => dispatch(requestError(response.data.error)) );
  }
}

//发票列表
export function fetchInvoiceList(page) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/invoice-list/${page}`)
      .then(response => dispatch({ type: FETCH_INVOICE_LIST, payload:response }))
      .catch( response => dispatch(requestError(response.data.error)) );
  }
}


