/**
 * oxchain ivoice app
 *
 * 自动开票
 *
 * Author: Jun
 * Email: iyakexi@gmail.com
 * Date: 13/04/2017
 *
 */

import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { autoAction } from '../actions/invoice';

class Auto extends Component {

  handleFormSubmit(values) {
    this.props.autoAction(values);
  }

  renderAlert() {
    if(!this.props.message) return;
    if (!this.props.success) {
      return (<div className="alert alert-danger margin">
        <button type="button" className="close" data-dismiss="alert" aria-label="Close"></button>
        {this.props.message}
      </div>);
    } else {
      return (<div className="alert alert-success margin">
        <button type="button" className="close" data-dismiss="alert" aria-label="Close"></button>
        出票成功
      </div>);
    }
  }

  renderField({ input, label, type, required, meta: { touched, error } }) {
    return (
      <div className={`form-group has-feedback ${touched && error ? 'has-error' : ''}`}>
        <label htmlFor="name" className="col-sm-3 control-label"><b className="text-danger">{required?'*':''}</b> {label}</label>
        <div className="col-sm-9 input-group">
          <input {...input} placeholder={label} type={type} className="form-control" placeholder={label}/>
          <div className="help-block with-errors">{touched && error ? error : ''}</div>
        </div>
      </div>
    )}

  render() {

    return (<div className="row">
      <div className="col-md-6 col-md-offset-3">
        <div className="box box-info">
          <div className="box-header with-border text-center"><h3 className="box-title">自动开票</h3></div>

          {this.renderAlert()}

          <form role="form" data-toggle="validator" className="form-horizontal"  onSubmit={this.props.handleSubmit(this.handleFormSubmit.bind(this))}>
            <div className="box-body">
              <Field name="mobile" component={this.renderField} type="text"  label="持票人手机" required={true}/>
              <Field name="receipt" component={this.renderField} type="text"  label="购物小票号码" required={true}/>
              <Field name="organization" component={this.renderField} type="text"  label="单位全称" required={true}/>
            </div>

            <div className="box-footer text-center">
              <button type="submit" className="btn btn-success">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>);
  }
};

const validate = values => {
  const errors = {};

  if(!values.mobile) {
    errors.mobile = '手机号不能为空';
  }

  if(!values.receipt) {
    errors.receipt = '购物小票号码不能为空';
  }

  if(!values.organization) {
    errors.organization = '单位全称不能为空';
  }

  return errors
};

function mapStateToProps(state) {
  return {
    success: state.invoice.success,
    message: state.invoice.message
  };
}

const reduxAutoForm = reduxForm({
  form: 'AutoForm',
  validate
})(Auto);

export default connect(mapStateToProps, { autoAction })(reduxAutoForm);