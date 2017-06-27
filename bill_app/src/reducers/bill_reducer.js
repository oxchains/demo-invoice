import {
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
} from '../actions/types';

const INITIAL_STATE = { chain: null, bills: null, bill: null, acceptance:null, guaranty:null, revocation:null, reception:null,
  endorsement:null, discount:null, pledge:null, release:null, payment:null, recourse:null
};

export default function(state = INITIAL_STATE, action) {
  switch(action.type) {
    case FETCH_BILL_LIST:
      return { ...state, bills:action.payload.data.data };
    case FETCH_BILL_INFO:
      return { ...state, bill:action.payload.data.data };
    case FETCH_ACCEPTANCE_LIST:
      return { ...state, acceptance:action.payload.data.data };
    case FETCH_GUARANTY_LIST:
      return { ...state, guaranty:action.payload.data.data };
    case FETCH_REVOCATION_LIST:
      return { ...state, revocation:action.payload.data.data };
    case FETCH_RECEPTION_LIST:
      return { ...state, reception:action.payload.data.data };
    case FETCH_ENDORSEMENT_LIST:
      return { ...state, endorsement:action.payload.data.data };
    case FETCH_DISCOUNT_LIST:
      return { ...state, discount:action.payload.data.data };
    case FETCH_PLEDGE_LIST:
      return { ...state, pledge:action.payload.data.data };
    case FETCH_RELEASE_LIST:
      return { ...state, release:action.payload.data.data };
    case FETCH_PAYMENT_LIST:
      return { ...state, payment:action.payload.data.data };
    case FETCH_RECOURSE_LIST:
      return { ...state, recourse:action.payload.data.data };
  }

  return state;
}