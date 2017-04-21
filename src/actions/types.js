/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 19/04/2017
 *
 */

export const ROOT_URL = 'http://localhost:3000';

export const AUTH_USER = 'auth_user';                               //登录
export const UNAUTH_USER = 'unauth_user';                           //退出登录
export const AUTH_ERROR = 'auth_error';                             //登录失败
export const REQUEST_SUCCESS = 'request_success';                   //http请求正确
export const REQUEST_ERROR = 'request_error';                       //http请求返回错误
export const FETCH_INVOICE_LIST = 'fetch_invoice_list';             //获取发票列表
export const INVOICE_AUTO = 'invoice_auto';                         //自动开票
export const FETCH_REIMBURSE_LIST = 'fetch_reimburse_list';         //获取企业报销列表
export const FETCH_REIMBURSE = 'fetch_reimburse';                   //获取报销详情
export const FETCH_MY_REIMBURSE_LIST = 'fetch_my_reimburse_list';   //获取我的报销列表
