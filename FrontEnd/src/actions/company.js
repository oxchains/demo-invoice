/**
 * oxchain
 *
 *
 * Author: Jun
 * Date: 30/06/2017
 *
 */

import axios from 'axios';
import { browserHistory } from 'react-router';
import {
  ROOT_URL,
  REQUEST_SUCCESS,
  REQUEST_ERROR,
  FETCH_COMPANY_LIST,
  getAuthorizedHeader,
  requestError
} from './types';

/**
 * 公司列表
 * @returns {Function}
 */
export function fetchCompanyList() {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/company`, { headers: getAuthorizedHeader() })
      .then(response => dispatch({ type: FETCH_COMPANY_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}