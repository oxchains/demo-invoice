/**
 * oxchain
 *
 *
 * Author: Jun
 * Date: 30/06/2017
 *
 */


import React, { Component } from 'react';
import { Field, reduxForm, formValueSelector } from 'redux-form';
import { connect } from 'react-redux';
import { transferAction } from '../actions/invoice';

class InvoiceTransfer extends Component {
  constructor(props) {
    super(props);
    this.state = { error:null, spin:false };
  }

  componentWillMount() {
  }

  componentWillReceiveProps(props) {
    if(!props.selectedItem) {
      this.setState({error:null});
    }
  }

  handleFormSubmit({ target, biz }) {
    if(!this.props.selectedItem) return;

    const { serial } = this.props.selectedItem

    this.setState({ spin:true });
    this.props.transferAction({ serial, target, biz }, err => {
      this.setState({ error: err ? err : null, spin:false });
      if(typeof this.props.actionCallback === 'function') this.props.actionCallback(err);
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
        <label>{label}</label>
        <input {...input} placeholder={label} type={type} className="form-control"/>
        <div className="help-block with-errors">{touched && error ? error : ''}</div>
      </div>
    )
  }


  renderInvoice(  ) {
    const { selectedItem } = this.props;
    if(!selectedItem) return null;
    return <div className={`form-group has-feedback`}>
      <label>发票</label>
      <span className="form-control">{selectedItem.serial}</span>
    </div>
  }

  render() {
    const { handleSubmit} = this.props;
    return (
      <div>
        <div className="">
          <div className="">
            {this.renderAlert()}
            <form className="form-signin" onSubmit={handleSubmit(this.handleFormSubmit.bind(this))}>
              {this.renderInvoice()}
              <Field name="target" component={this.renderField.bind(this)} type="text"  label="流转给" />
              <div className="form-group">
                <label className="margin-r-5"><Field name="biz" component="input" type="radio" value=""/> 个人</label>
                <label className="margin-r-5"><Field name="biz" component="input" type="radio" value="1"/> 企业</label>
              </div>
              <div className="row">
                <div className="col-xs-8">
                  {this.state.spin?'执行中...':''}
                </div>
                <div className="col-xs-4">
                  <button type="submit" className="btn btn-success pull-right"><i className={`fa fa-spinner fa-spin ${this.state.spin?'':'hidden'}`}></i> 提交 </button>
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

  if(!values.target) {
    errors.target = '请填写接收人或企业名称';
  }

  return errors
};

const selector = formValueSelector('transferActionForm') ;
const reduxInvoiceTransferForm = reduxForm({
  form: 'transferActionForm',
  validate
})(InvoiceTransfer);

function mapStateToProps(state) {
  return {
    selectedChain:selector(state, 'chain')
  };
}

export default connect(mapStateToProps, { transferAction })(reduxInvoiceTransferForm);