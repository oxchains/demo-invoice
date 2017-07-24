/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 19/04/2017
 *
 */

export const ROOT_URL = 'http://xfja.local:11919';

export const AUTH_USER = 'auth_user';                               //登录
export const UNAUTH_USER = 'unauth_user';                           //退出登录
export const AUTH_ERROR = 'auth_error';                             //登录失败
export const REQUEST_SUCCESS = 'request_success';                   //http请求正确
export const REQUEST_ERROR = 'request_error';                       //http请求返回错误
export const FETCH_INVOICE_LIST = 'fetch_invoice_list';             //获取发票列表
export const FETCH_INVOICE_DETAIL = 'fetch_invoice_detail';         //获取发票详情
export const INVOICE_AUTO = 'invoice_auto';                         //自动开票
export const FETCH_REIMBURSE_LIST = 'fetch_reimburse_list';         //获取企业报销列表
export const FETCH_REIMBURSE = 'fetch_reimburse';                   //获取报销详情
export const FETCH_MY_REIMBURSE_LIST = 'fetch_my_reimburse_list';   //获取我的报销列表
export const SELECT_INVOICE = 'select_invoice';                     //选择发票
export const DESELECT_INVOICE = 'deselect_invoice';                 //取消选择发票
export const REIMBURSE_SUCCESS = 'reimburse_success';               //报销成功
export const FETCH_COMPANY_LIST = 'fetch_company_list';             //获取公司列表



export function getAuthorizedHeader() {
  return { authorization: 'Bearer '+localStorage.getItem('token') }
}

export function requestError(error) {
  return {
    type: REQUEST_ERROR,
    payload: error
  };
}
