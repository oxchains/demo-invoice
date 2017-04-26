/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Email: iyakexi@gmail.com
 * Date: 20/04/2017
 *
 */

import _ from 'lodash';
import {
  REQUEST_SUCCESS,
  REQUEST_ERROR,
  FETCH_INVOICE_LIST,
  SELECT_INVOICE,
  DESELECT_INVOICE
} from '../actions/types';

const INITIAL_STATE = { all: [], item: null, selectedIds:[] };

export default function(state = INITIAL_STATE, action) {
  switch(action.type) {
    case REQUEST_SUCCESS:
      return { ...state, message: '操作成功', success:1 };
    case REQUEST_ERROR:
      return { ...state, message: action.payload, success:0 };
    case FETCH_INVOICE_LIST:
      return { ...state, all:action.payload.data.data, pageCount:action.payload.data.totalPage, selectedIds:[] };
    case SELECT_INVOICE:
      return { ...state, selectedIds:[...state.selectedIds, action.payload] };
    case DESELECT_INVOICE:
      return { ...state, selectedIds: _.without(state.selectedIds, action.payload) };
  }

  return state;
}