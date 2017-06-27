/**
 * oxchain
 *
 *
 * Author: Jun
 * Date: 15/06/2017
 *
 */

import React, { Component } from 'react';
import { Field, reduxForm, formValueSelector } from 'redux-form';
import { connect } from 'react-redux';
import { prompt_endorsement } from '../../actions/bill'

class PromptEndorsement extends Component {
  constructor(props) {
    super(props);
    this.state = { error:null, spin:false };
  }

  handleFormSubmit({ id, manipulator, endorsor, action }) {
    if(id && manipulator) {
      let endorsee = action==1?endorsor:null;
      endorsor = action==1?null:endorsor;
      this.setState({ spin:true });
      this.props.prompt_endorsement({ id, manipulator, endorsor, endorsee, action }, err => {
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
    const { error, handleSubmit, pristine, reset, submitting, action } = this.props;

    return (
      <div className="">
        {this.renderAlert()}
        <form className="form-horizontal" onSubmit={handleSubmit(this.handleFormSubmit.bind(this))}>
          <Field name="id" component={this.renderField} type="text" label="票据编号" />
          <Field name="manipulator" component={this.renderField} type="text"  label="操作者" />
          <div className="form-group">
            <label className="col-sm-2 control-label"></label>
            <div className="col-sm-10">
              <label className="margin-r-5"><Field name="action" component="input" type="radio" value=""/> 不限制转让</label>
              <label className="margin-r-5"><Field name="action" component="input" type="radio" value="-1"/> 不得继续转让</label>
              <label className="margin-r-5"><Field name="action" component="input" type="radio" value="0"/> 撤销</label>
              <label className="margin-r-5"><Field name="action" component="input" type="radio" value="1"/> 确认</label>
            </div>
          </div>
          <Field name="endorsor" component={this.renderField} type="text"  label={action==1?'被背书人':'背书人'} />
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

  if(!values.endorsor) {
    errors.endorsor = '不能为空';
  }

  return errors
};

const selector = formValueSelector('PromptEndorsementForm');

const reduxPromptEndorsementForm = reduxForm({
  form: 'PromptEndorsementForm',
  validate
})(PromptEndorsement);

export default connect(state => { return {action:selector(state, 'action')} }, { prompt_endorsement })(reduxPromptEndorsementForm);