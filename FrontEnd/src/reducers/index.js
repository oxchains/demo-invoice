import { combineReducers } from 'redux';
import { reducer as formReducer } from 'redux-form'
import authReducer from './auth_reducer';
import invoiceReducer from './invoice_reduicer';
import reimburseReducer from './reimburse_reduicer';
import companyReducer from './company_reduicer';

const rootReducer = combineReducers({
  form: formReducer,
  auth: authReducer,
  invoice: invoiceReducer,
  reimburse: reimburseReducer,
  company: companyReducer
});

export default rootReducer;
