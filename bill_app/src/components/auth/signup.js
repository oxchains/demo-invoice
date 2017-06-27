import React, {Component} from 'react';
import _ from 'lodash';
import {Field, reduxForm} from 'redux-form';
import {connect} from 'react-redux';
import {Redirect} from 'react-router-dom'
import {usersignUp} from '../../actions/user'

class Signup extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isAddFail: null
    }
  }

  componentWillUnmount() {
    this.setState({
      isAddFail: null
    });
  }

  handleFormSubmit({username, asset}) {
    if (username && asset > 0)
      this.props.usersignUp({username, asset}, ({isAddFail}) => {
        if(isAddFail){
          this.setState({
            isAddFail
          });
        }else{
          this.props.history.replace("/signin");
        }
      });
  }


  renderAlert() {
    if (this.state.isAddFail) {
      return (
        <div className="alert alert-danger alert-dismissable">
          注册失败
        </div>
      );
    }
  }

  renderField({input, label, type, icon, meta: {touched, error}}) {
    return (
      <div className={`form-group has-feedback ${touched && error ? 'has-error' : ''}`}>
        <input {...input} placeholder={label} type={type} className="form-control"/>
        <span className={`glyphicon glyphicon-${icon} form-control-feedback`}></span>
        <div className="help-block with-errors">{touched && error ? error : ''}</div>
      </div>
    )
  }

  render() {
    const {handleSubmit} = this.props;

    return (
      <div>
        <div className="login-box">
          <div className="login-logo">
          </div>
          <div className="login-box-body">
            <p className="login-box-msg" style={{fontSize: 24 + 'px'}}>用户注册</p>
            {this.renderAlert()}
            <form className="form-signin" onSubmit={handleSubmit(this.handleFormSubmit.bind(this))}>
              <Field name="username" component={this.renderField} type="text" label="用户名" icon="envelope"/>
              <Field name="asset" component={this.renderField} type="number" label="资产" icon="credit-card"/>
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

  if (!values.username) {
    errors.username = '用户名不能为空';
  }

  const asset = _.toInteger(values.asset);

  if (asset <= 0) {
    errors.asset = '资产不能为空';
  }
  return errors
};

const reduxSignupForm = reduxForm({
  form: 'SignForm',
  validate
})(Signup);

export default connect(null, {usersignUp})(reduxSignupForm);