/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Email: iyakexi@gmail.com
 * Date: 20/04/2017
 *
 */

import {
  REQUEST_SUCCESS,
  REQUEST_ERROR,
  FETCH_REIMBURSE_LIST,
  FETCH_REIMBURSE,
  FETCH_MY_REIMBURSE_LIST
} from '../actions/types';

const INITIAL_STATE = { all: [], item: null };

export default function(state = INITIAL_STATE, action) {
  switch(action.type) {
    case FETCH_REIMBURSE_LIST:
      return { ...state, all:action.payload.data.data, pageCount:action.payload.data.totalPage };
    case FETCH_REIMBURSE:
      return { ...state, item:action.payload.data.data };
    case FETCH_MY_REIMBURSE_LIST:
      return { ...state, all:action.payload.data.data, pageCount:action.payload.data.totalPage };
  }

  return state;
}