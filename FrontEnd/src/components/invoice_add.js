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
import { addAction } from '../actions/invoice';
import {
  Modal,
  ModalHeader,
  ModalTitle,
  ModalClose,
  ModalBody,
  ModalFooter
} from 'react-modal-bootstrap';
import CompanySelector from './company_selector';


class InvoiceAdd extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isModalOpen: false,
      spin : false,
      error: null,
      actionResult: ''
    };
  }

  hideModal = () => {
    this.setState({
      isModalOpen: false
    });
  };

  handleFormSubmit({title, target, name, quantity, price, description }) {
    this.setState({ spin:true });
    this.props.addAction({title, target, 'goods':[{name, quantity, price, description}] }, err=>{
      this.setState({ isModalOpen: true , error: err , actionResult: err||'开票成功!' , spin:false });
    });
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
      <div className="col-lg-8 col-lg-offset-2 col-xs-10 col-xs-offset-1">
        <div className="box box-info">
          <div className="box-header with-border text-center"><h3 className="box-title">自动开票</h3></div>

          <form role="form" data-toggle="validator" className="form-horizontal"  onSubmit={this.props.handleSubmit(this.handleFormSubmit.bind(this))}>
            <div className="box-body">
              <Field name="title" component={CompanySelector} className="form-control" label="发票抬头" required={true}/>
              <Field name="target" component={this.renderField} type="text"  label="发票接收人" required={true}/>
              <Field name="name" component={this.renderField} type="text"  label="商品名称" required={true}/>
              <Field name="price" component={this.renderField} type="number"  label="商品金额" required={true}/>
              <Field name="quantity" component={this.renderField} type="text"  label="商品数量" required={false}/>
              <Field name="description" component={this.renderField} type="text"  label="商品描述" required={false}/>
            </div>

            <div className="box-footer text-center">
              <button type="submit" className="btn btn-success"><i className={`fa fa-spinner fa-spin ${this.state.spin?'':'hidden'}`}></i> 开票</button>
            </div>
          </form>
        </div>
      </div>

      <Modal isOpen={this.state.isModalOpen} onRequestHide={this.hideModal}>
        <ModalHeader>
          <ModalClose onClick={this.hideModal}/>
          <ModalTitle>提示:</ModalTitle>
        </ModalHeader>
        <ModalBody>
          <p className={this.state.error?'text-red':'text-green'}>
            {this.state.actionResult}
          </p>
        </ModalBody>
        <ModalFooter>
          <button className='btn btn-default' onClick={this.hideModal}>
            关闭
          </button>
        </ModalFooter>
      </Modal>

    </div>);
  }
};

const validate = values => {
  const errors = {};

  if(!values.title) {
    errors.title = '发票抬头不能为空';
  }

  if(!values.target) {
    errors.target = '发票接收人不能为空';
  }

  if(!values.name) {
    errors.name = '商品名称不能为空';
  }

  if(!values.price) {
    errors.price = '商品金额不正确';
  }

  return errors
};

const reduxInvoiceAddForm = reduxForm({
  form: 'InvoiceAddForm',
  validate
})(InvoiceAdd);

export default connect(null, { addAction })(reduxInvoiceAddForm);