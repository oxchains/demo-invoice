import {
  REQUEST_SUCCESS,
  REQUEST_ERROR,
  FETCH_USER_LIST,
  ADD_USER_SUCCESS,
  ADD_USER_ERROR
} from '../actions/types';

const INITIAL_STATE = { all: null };

export default function(state = INITIAL_STATE, action) {
  switch(action.type) {
    case FETCH_USER_LIST:
      return { ...state, all:action.payload.data.data };
    case ADD_USER_SUCCESS:
      return { ...state, addSuccess: true};
    case ADD_USER_ERROR:
      return { ...state, addSuccess: false};
  }

  return state;
}