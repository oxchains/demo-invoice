import axios from 'axios';
import {
  ROOT_URL,
  REQUEST_SUCCESS,
  REQUEST_ERROR,
  FETCH_BILL_LIST,
  FETCH_BILL_INFO,
  FETCH_ACCEPTANCE_LIST,
  FETCH_GUARANTY_LIST,
  FETCH_REVOCATION_LIST,
  FETCH_RECEPTION_LIST,
  FETCH_ENDORSEMENT_LIST,
  FETCH_DISCOUNT_LIST,
  FETCH_PLEDGE_LIST,
  FETCH_RELEASE_LIST,
  FETCH_PAYMENT_LIST,
  FETCH_RECOURSE_LIST
} from './types';

export function requestError(error) {
  return {
    type: REQUEST_ERROR,
    payload: error
  };
}

//Bill列表
//export function fetchBillList() {
//  return function(dispatch) {
//    axios.get(`${ROOT_URL}/bills/`)
//      .then(response => dispatch({ type: FETCH_BILL_LIST, payload:response }))
//      .catch( err => dispatch(requestError(err.message)) );
//  }
//}


/**
 *
 * @param price           金额
 * @param drawee          承兑人
 * @param payee           收款人
 * @param due             票据到期日
 * @param transferable    是否可转移
 * @param callback
 * @returns {Function}
 */
export function register({ price, drawee, drawer, payee, due, transferable}, callback) {
  due = due+' 23:59:59';
  return function(dispatch) {
    axios.post(`${ROOT_URL}/bill`, { price, drawer, drawee, payee, due, transferable})
      .then(response => {
        if(response.data.status == 1) {// success
          callback();
        } else {// fail
          callback(response.data.message);
        }
      })
      .catch(err => {
        dispatch(requestError(err.message));
        callback(err.message);
      });
  }
}


/**
 * 操作票据统一调用接口
 * @param url
 * @param params
 * @param callback
 * @returns {Function}
 */
function bill_action(url, params, callback) {
  return function(dispatch) {
    axios.post(url, params)
      .then(response => {
        if(response.data.status == 1) {// success
          callback();
        } else {// fail
          callback(response.data.message);
        }
      })
      .catch(err => {
        dispatch(requestError(err.message));
        callback(err.message);
      });
  }
}

/**
 * 提示承兑/撤销; 确认承兑
 * @param id            票据编号ID
 * @param manipulator   操作者(出票人|保证人)
 * @param action        操作类型：null:提示承兑(出票人), 0:撤销(出票人), 1:确认(保证人)
 * @param callback
 */
export function prompt_acceptance({id, manipulator, action}, callback) {
  return bill_action(`${ROOT_URL}/prompt_acceptance`, {id, manipulator, action}, callback);
}

/**
 * 提示保证、撤销、确认
 * @param id            票据编号ID
 * @param manipulator   操作者(出票人|保证人)
 * @param action        操作类型：null:提示(出票人), 0:撤销(出票人), 1:确认(保证人)
 * @param callback
 */
export function prompt_warrant({id, manipulator, action}, callback) {
  return bill_action(`${ROOT_URL}/prompt_warrant`, {id, manipulator, action}, callback);
}

/**
 * 出票人撤票
 * @param id            票据编号ID
 * @param manipulator   操作者(出票人)
 * @param callback
 */
export function prompt_revoke({id, manipulator}, callback) {
  return bill_action(`${ROOT_URL}/prompt_revoke`, {id, manipulator}, callback);
}


/**
 * 提示收票、撤销、确认
 * @param id            票据编号ID
 * @param manipulator   操作者(出票人)
 * @param action        操作类型：null:提示(出票人), 0:撤销(出票人), 1:确认(出票人)
 * @param callback
 */
export function prompt_receive({id, manipulator, action}, callback) {
  return bill_action(`${ROOT_URL}/prompt_receive`, {id, manipulator, action}, callback);
}

/**
 * 提示背书、撤销、确认
 * @param id            票据编号ID
 * @param manipulator   操作者
 * @param endorsor      背书人
 * @param endorsee      被背书人
 * @param action        操作类型：null:不限制转让(背书人), -1:不得继续转让(背书人), 0:撤销(背书人), 1:确认(被背书人)
 * @param callback
 * @returns {Function}
 */
export function prompt_endorsement({id, manipulator, endorsor, endorsee, action}, callback) {
  let params = null;
  if(action==1) params = {id, manipulator, endorsee, action};
  else params = {id, manipulator, endorsor, action};
  return bill_action(`${ROOT_URL}/prompt_endorsement`, params, callback);
}

/**
 * 提示贴现、撤销、确认
 * @param id            票据编号ID
 * @param manipulator   操作者
 * @param action        操作类型：null:不限制转让(贴出人)，-1:不得继续转让(贴出人)，0：撤销(贴出人), 1:确认(贴入人)
 * @param discount_type 贴现类型
 * @param receiver      贴入人
 * @param discount_interest 贴现利率
 * @param money         实付金额
 * @param callback
 * @returns {Function}
 */
export function prompt_discount({id, manipulator, action, discount_type, receiver, discount_interest, money}, callback) {
  let params = null;
  if(action==1) params = {id, manipulator, action};
  else params = {id, manipulator, action, discount_type, receiver, discount_interest, money};
  return bill_action(`${ROOT_URL}/prompt_discount`, params, callback);
}

/**
 * 提示质押、撤销、确认
 * @param id            票据编号ID
 * @param manipulator   操作者
 * @param pledgee       质权人
 * @param action        操作类型：null:提示质押(出质人) 0:撤销(出质人), 1:确认(质权人)
 * @param callback
 */
export function prompt_pledge({id, manipulator, action, pledgee}, callback) {
  return bill_action(`${ROOT_URL}/prompt_pledge`, {id, manipulator, action, pledgee}, callback);
}

/**
 * 提示质押解除、撤销、确认
 * @param id            票据编号ID
 * @param manipulator   操作者
 * @param action        操作类型：null:提示质押(质权人) 0:撤销(质权人), 1:确认(出质人)
 * @param callback
 */
export function prompt_pledge_release({id, manipulator, action}, callback) {
  return bill_action(`${ROOT_URL}/prompt_pledge_release`, {id, manipulator, action}, callback);
}

/**
 * 提示付款、撤销、确认
 * @param id            票据编号ID
 * @param manipulator   操作者
 * @param action        操作类型：null:提示付款(持票人) 0:撤销(持票人), 1:确认(承兑人、被追索人)
 * @param callback
 */
export function prompt_pay({id, manipulator, action}, callback) {
  return bill_action(`${ROOT_URL}/prompt_pay`, {id, manipulator, action}, callback);
}

/**
 * 提示追索、撤销
 * @param id            票据编号ID
 * @param manipulator   操作者
 * @param debtor        被追索人（前手或出票人）
 * @param action        操作类型：null:提示追索(持票人) 0:撤销(持票人)
 * @param callback
 */
export function prompt_dun({id, manipulator, action, debtor}, callback) {
  return bill_action(`${ROOT_URL}/prompt_dun`, {id, manipulator, action, debtor}, callback);
}

/**
 * 票据详情
 * @param id
 */
export function fetchBillInfo(id) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${id}`)
      .then(response => dispatch({ type: FETCH_BILL_INFO, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * (待)承兑列表
 * @param username
 */
export function fetchAcceptanceList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/acceptance`)
      .then(response => dispatch({ type: FETCH_ACCEPTANCE_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * (待)保证列表
 * @param username
 */
export function fetchGuarantyList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/guaranty`)
      .then(response => dispatch({ type: FETCH_GUARANTY_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * 撤票列表
 * @param username
 */
export function fetchRevocationList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/revocation`)
      .then(response => dispatch({ type: FETCH_REVOCATION_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * (待)收票列表
 * @param username
 */
export function fetchReceptionList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/reception`)
      .then(response => dispatch({ type: FETCH_RECEPTION_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * (待)背书列表
 * @param username
 */
export function fetchEndorsementList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/endorsement`)
      .then(response => dispatch({ type: FETCH_ENDORSEMENT_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * (待)贴现列表
 * @param username
 */
export function fetchDiscountList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/discount`)
      .then(response => dispatch({ type: FETCH_DISCOUNT_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * (待)质押列表
 * @param username
 */
export function fetchPledgeList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/pledge`)
      .then(response => dispatch({ type: FETCH_PLEDGE_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * (待)质押解除列表
 * @param username
 */
export function fetchReleaseList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/release`)
      .then(response => dispatch({ type: FETCH_RELEASE_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * (待)支付列表
 * @param username
 */
export function fetchPaymentList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/payment`)
      .then(response => dispatch({ type: FETCH_PAYMENT_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

/**
 * (待)追索列表
 * @param username
 */
export function fetchRecourseList(username) {
  return function(dispatch) {
    axios.get(`${ROOT_URL}/bill/${username}/recourse`)
      .then(response => dispatch({ type: FETCH_RECOURSE_LIST, payload:response }))
      .catch( err => dispatch(requestError(err.message)) );
  }
}

