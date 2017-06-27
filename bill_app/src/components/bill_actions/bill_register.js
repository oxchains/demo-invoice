import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { register } from '../../actions/bill'
import DatePicker from 'react-datepicker';
import moment from 'moment';

import '../../../node_modules/react-datepicker/dist/react-datepicker.css';

class BillRegister extends Component {
  constructor(props) {
    super(props);
    this.state = { error:null, spin:false, dueDate: moment() };
  }

  handleFormSubmit({ price, drawee, drawer, payee, transferable }) {
    if(price && drawer && drawee && payee) {
      const due = this.state.dueDate.format('YYYY-MM-DD');
      transferable = !!transferable;
      this.setState({ spin:true });
      this.props.register({ price, drawer, drawee, payee, transferable, due }, err => {
        this.setState({ error: err ? err : null, spin:false });
        if(typeof this.props.addCallback == 'function') this.props.addCallback(err);
      });
    }
  }

  handleDateChange(date) {
    this.setState({
      dueDate: date
    });
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
    )}

  render() {
    const { handleSubmit} = this.props;

    return (
        <div className="">
          {this.renderAlert()}
          <form className="form-horizontal" onSubmit={handleSubmit(this.handleFormSubmit.bind(this))}>
            <Field name="price" component={this.renderField} type="number"  label="金额" />
            <Field name="drawer" component={this.renderField} type="text"  label="出票人" />
            <Field name="drawee" component={this.renderField} type="text"  label="承兑人" />
            <Field name="payee" component={this.renderField} type="text" label="收款人" />
            <div className={`form-group has-feedback`}>
              <label className="col-sm-2 control-label">票据到期日 </label>
              <div className="col-sm-10">
                <DatePicker
                  selected={this.state.dueDate}
                  onChange={this.handleDateChange.bind(this)}
                  placeholderText="票据到期日"
                  dateFormat="YYYY-MM-DD"
                />
              </div>
            </div>
            <div className="form-group">
              <label className="col-sm-2 control-label">是否可转移</label>
              <div className="col-sm-10">
                <Field name="transferable" id="transferable" component="input" type="checkbox"/>
              </div>
            </div>
            <div className="row">
              <div className="col-xs-8">
              </div>
              <div className="col-xs-4">
                <button type="submit" className="btn btn-primary btn-block btn-flat"><i className={`fa fa-spinner fa-spin ${this.state.spin?'':'hidden'}`}></i> 确认出票 </button>
              </div>
            </div>

          </form>
        </div>
    );
  }
}


const validate = values => {
  const errors = {};

  if(!values.price) {
    errors.price = '金额不能为空';
  }

  if(!values.drawer) {
    errors.drawer = '出票人不能为空';
  }

  if(!values.drawee) {
    errors.drawee = '承兑人不能为空';
  }

  if(!values.payee) {
    errors.payee = '收款人不能为空';
  }

  if(!values.due) {
    errors.due = '票据到期日不能为空';
  }

  return errors
};


const reduxBillRegisterForm = reduxForm({
  form: 'BillRegisterForm',
  validate
})(BillRegister);

export default connect(null, { register })(reduxBillRegisterForm);