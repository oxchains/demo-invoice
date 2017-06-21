import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { signupUser } from '../../actions/auth'

class Signup extends Component {

  handleFormSubmit({ username, mobile, password }) {
    if(username && password && mobile)
      this.props.signupUser({ username, mobile, password });
  }

  renderAlert() {
    if (this.props.errorMessage) {
      return (
        <div className="alert alert-danger alert-dismissable">
          {this.props.errorMessage}
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
        <div className="login-box">
          <div className="login-logo">
          </div>
          <div className="login-box-body">
            <p className="login-box-msg" style={{fontSize: 24+'px'}}>用户注册</p>
            {this.renderAlert()}
            <form className="form-signin" onSubmit={handleSubmit(this.handleFormSubmit.bind(this))}>
              <Field name="username" component={this.renderField} type="text"  label="用户名" icon="envelope" />
              <Field name="mobile" component={this.renderField} type="text"  label="手机号" icon="phone" />
              <Field name="password" component={this.renderField} type="password" label="密码" icon="lock" />
              <Field name="passwordConfirm" component={this.renderField} type="password" label="确认密码" icon="lock" />
              <div className="row">
                <div className="col-xs-8">
                </div>
                <div className="col-xs-4">
                  <button type="submit" className="btn btn-primary btn-block btn-flat">注册</button>
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

  if(!values.mobile) {
    errors.mobile = '手机号不能为空';
  }

  if(!values.password) {
    errors.password = '密码不能为空';
  }

  if(!values.passwordConfirm) {
    errors.passwordConfirm = '确认密码不能为空';
  }

  if(values.passwordConfirm && values.passwordConfirm != values.password) {
    errors.passwordConfirm = '两次输入密码不一致';
  }

  return errors
};

function mapStateToProps(state) {
  return {
    success: state.auth.authenticated,
    errorMessage: state.auth.error
  };
}

const reduxSignupForm = reduxForm({
  form: 'SignForm',
  validate
})(Signup);

export default connect(mapStateToProps, { signupUser })(reduxSignupForm);