import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';
import {createStore, applyMiddleware, compose} from 'redux';
import {Route, BrowserRouter, Switch, Redirect} from 'react-router-dom';
import reduxThunk from 'redux-thunk';

import reducers from './reducers';
import {AUTH_USER} from './actions/types';

import RequireAuth from './components/auth/require_auth';
import PrivateRoute from './components/auth/private_route';
import Welcome from './components/welcome';
import NavTop from './components/common/header';
import NavSide from './components/common/nav_side';
import Footer from './components/common/footer';
import Signout from './components/auth/signout';
import Signin from './components/auth/signin';
import Signup from './components/auth/signup';
import UserList from './components/user_list';
import BillList from './components/bill_list';
import BillActions from './components/bill_actions';

import {register} from './actions/registerService';

const createStoreWithMiddleware = compose(
  applyMiddleware(reduxThunk),
  window.devToolsExtension ? window.devToolsExtension() : f => f
)(createStore);
const store = createStoreWithMiddleware(reducers);

ReactDOM.render(
  <Provider store={store}>
    <BrowserRouter>
      <div>
        <NavTop />
        <NavSide/>
        <div className="content-wrapper">
          <Switch>
            <Route path="/signout" component={Signout}/>
            <Route path="/signin" component={Signin}/>
            <Route path="/signup" component={Signup}/>
            <Route path="/users" component={UserList}/>
            <Route path="/bill/actions" component={BillActions}/>
            <Route path="/bills" component={BillList}/>
            <Route path="/" component={Welcome}/>
          </Switch>
        </div>
        <Footer/>
      </div>
    </BrowserRouter>
  </Provider>
  , document.querySelector('.wrapper'));

//如果之前已经登录成功 则自动登录
const username = localStorage.getItem('username');
// If token exist, singin automatic
if (username) {
  store.dispatch({type: AUTH_USER, username});
  register(username);
}

