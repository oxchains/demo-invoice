/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 03/05/2017
 *
 */


import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { addUser } from '../actions/user'

class AddUser extends Component {
  constructor(props) {
    super(props);
    this.state = { error:null, spin:false };
  }

  handleFormSubmit({ username, affiliation, password }) {
    if(username && password && affiliation) {
      this.setState({ spin:true });
      this.props.addUser({username, affiliation, password}, err => {
        this.setState({ error: err ? err : null, spin:false });
        this.props.addCallback(err);
      });
    }
  }

  renderAlert() {
    if (this.state.error) {
      return (
        <div className="alert alert-danger alert-dismissable">
          {this.state.error}
        </div>
      );
    }
  }

  renderField({ input, label, type, icon, meta: { touched, error } }) {
    return (
      <div className={`form-group has-feedback ${touched && error ? 'has-error' : ''}`}>
        <input {...input} placeholder={label} type={type} className="form-control"/>
        <span className={`glyphicon glyphicon-${icon} form-control-feedback`}></span>
        <div className="help-block with-errors">{touched && error ? error : ''}</div>
      </div>
    )}

  render() {
    const { handleSubmit} = this.props;

    return (
      <div>
        <div className="login-boxw">
          <div className="login-logo">
          </div>
          <div className="login-box-body">
            <p className="login-box-msg" style={{fontSize: 24+'px'}}>用户注册</p>
            {this.renderAlert()}
            <form className="form-signin" onSubmit={handleSubmit(this.handleFormSubmit.bind(this))}>
              <Field name="username" component={this.renderField} type="text"  label="用户名" icon="user" />
              <Field name="affiliation" component={this.renderField} type="text"  label="机构" icon="home" />
              <Field name="password" component={this.renderField} type="text" label="密码" icon="lock" />
              <div className="row">
                <div className="col-xs-8">
                </div>
                <div className="col-xs-4">
                  <button type="submit" className="btn btn-primary btn-block btn-flat"><i className={`fa fa-spinner fa-spin ${this.state.spin?'':'hidden'}`}></i> 注册 </button>
                </div>
              </div>

            </form>
          </div>
        </div>
      </div>
    );
  }
}


const validate = values => {
  const errors = {};

  if(!values.username) {
    errors.username = '用户名不能为空';
  }

  if(!values.affiliation) {
    errors.affiliation = '机构不能为空';
  }

  if(!values.password) {
    errors.password = '密码不能为空';
  }

  return errors
};


const reduxAddUserForm = reduxForm({
  form: 'SignupForm',
  validate
})(AddUser);

export default connect(null, { addUser })(reduxAddUserForm);