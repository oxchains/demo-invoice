/**
 * oxchain
 *
 *
 * Author: Jun
 * Date: 14/06/2017
 *
 */

import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { prompt_warrant } from '../../actions/bill'

class PromptWarrant extends Component {
  constructor(props) {
    super(props);
    this.state = { error:null, spin:false };
  }

  handleFormSubmit({ id, manipulator, action }) {
    if(id && manipulator) {
      this.setState({ spin:true });
      this.props.prompt_warrant({ id, manipulator, action }, err => {
        this.setState({ error: err ? err : null, spin:false });
        if(typeof this.props.addCallback == 'function') this.props.addCallback(err);
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

  renderField({ input, label, type, meta: { touched, error } }) {
    return (
      <div className={`form-group has-feedback ${touched && error ? 'has-error' : ''}`}>
        <label className="col-sm-2 control-label">{label}</label>
        <div className="col-sm-10">
          <input {...input} placeholder={label} type={type} className="form-control"/>
          <div className="help-block with-errors">{touched && error ? error : ''}</div>
        </div>
      </div>
    )
  }

  render() {
    const { error, handleSubmit, pristine, reset, submitting } = this.props;

    return (
      <div className="">
        {this.renderAlert()}
        <form className="form-horizontal" onSubmit={handleSubmit(this.handleFormSubmit.bind(this))}>
          <Field name="id" component={this.renderField} type="text" label="票据编号" />
          <Field name="manipulator" component={this.renderField} type="text"  label="操作者" />
          <div className="form-group">
            <label className="col-sm-2 control-label"></label>
            <div className="col-sm-10">
              <label className="margin-r-5"><Field name="action" component="input" type="radio" value=""/> 提示</label>
              <label className="margin-r-5"><Field name="action" component="input" type="radio" value="0"/> 撤销</label>
              <label className="margin-r-5"><Field name="action" component="input" type="radio" value="1"/> 确认</label>
            </div>
          </div>
          <div className="row">
            <div className="col-xs-8">
            </div>
            <div className="col-xs-4">
              <button type="submit" className="btn btn-primary btn-block btn-flat" disabled={submitting}><i className={`fa fa-spinner fa-spin ${this.state.spin?'':'hidden'}`}></i> 提交 </button>
            </div>
          </div>

        </form>
      </div>
    );
  }
}


const validate = values => {
  const errors = {};

  if(!values.id) {
    errors.id = '票据编号不能为空';
  }

  if(!values.manipulator) {
    errors.manipulator = '操作者不能为空';
  }

  return errors
};


const reduxPromptWarrantForm = reduxForm({
  form: 'PromptWarrantForm',
  validate
})(PromptWarrant);

export default connect(null, { prompt_warrant })(reduxPromptWarrantForm);