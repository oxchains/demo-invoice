import { combineReducers } from 'redux';
import { reducer as formReducer } from 'redux-form'
import authReducer from './auth_reducer';
import userReducer from './user_reducer';
import billReducer from './bill_reducer';

const rootReducer = combineReducers({
  form: formReducer,
  auth: authReducer,
  user: userReducer,
  bill: billReducer
});

export default rootReducer;
