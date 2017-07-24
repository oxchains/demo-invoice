/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Email: iyakexi@gmail.com
 * Date: 26/04/2017
 *
 */

import React, { Component } from 'react';
import { Field, reduxForm,formValueSelector } from 'redux-form';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import Moment from 'react-moment';
import _ from 'lodash';
import { reimburseAction } from '../actions/invoice';
import SelectedInvoicesSelector from '../selectors/selected_invoices';
import {
  Modal,
  ModalHeader,
  ModalTitle,
  ModalClose,
  ModalBody,
  ModalFooter
} from 'react-modal-bootstrap';
import CompanySelector from './company_selector';

class InvoiceList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isModalOpen: false,
      spin : false,
      error: null,
      actionResult: '',
      company: '',
      remark: ''
    };
  }

  handleReimburse() {
    console.log(this.props.selectedIds);
    this.setState({isModalOpen: true});
  }

  hideModal() {
    this.setState({isModalOpen: false, actionResult:''});
  }

  handleFormSubmit({ company, remark }) {
    if(this.props.selectedIds.length < 1) return;

    this.setState({ spin:true });
    this.props.reimburseAction(this.props.selectedIds, company, remark, err=>{
      this.setState({ error: err , actionResult: err||'报销申请提交成功!' , spin:false });
    });
  }

  renderRows() {
    return this.props.all.map((row, idx) => {
      return (<tr key={idx}>
        <td>{row.serial}</td>
        <td><Moment locale="zh-cn" format="lll">{row.createtime}</Moment></td>
      </tr>);
    });
  }

  render() {
    return (
        <div className="box box-info">
          <div className="box-header"><h3 className="box-title">已经选择发票</h3></div>
          <div className="box-body table-responsive no-padding">
            <table className="table table-bordered table-hover">
              <tbody>
              <tr>
                <th>发票编号</th>
                <th>开票时间</th>
              </tr>
              { this.renderRows() }
              </tbody>
            </table>
          </div>
          <div className="box-footer clearfix">
            <button className="btn btn-success pull-right"
                    disabled={this.props.selectedIds.length<1}
                    onClick={this.handleReimburse.bind(this)}>报销</button>
          </div>

          <Modal isOpen={this.state.isModalOpen} onRequestHide={this.hideModal.bind(this)}>
            <ModalHeader>
              <ModalClose onClick={this.hideModal.bind(this)}/>
              <ModalTitle>选择发票报销公司</ModalTitle>
            </ModalHeader>
            <ModalBody>
                <form className="form-signin"  onSubmit={this.props.handleSubmit(this.handleFormSubmit.bind(this))}>
                  <Field name="company" component={CompanySelector} className="form-control" label="发票报销公司" required={true} vertical={true} />
                  <div className={`form-group has-feedback `}>
                    <label className="col-sm-12 control-label">备注</label>
                    <div className='col-sm-12 input-group'>
                    <Field name="remark" component="input" type="text" className="form-control" placeHolder="备注"/>
                    </div>
                  </div>
                  <div className="row">
                    <div className="col-xs-8">
                      <div className={` ${this.state.error?'text-red':'text-green'}`}>{this.state.actionResult}</div>
                    </div>
                    <div className="col-xs-4">
                      <button type="submit" className="btn btn-success pull-right"><i className={`fa fa-spinner fa-spin ${this.state.spin?'':'hidden'}`}></i> 申请报销 </button>
                    </div>
                  </div>
                </form>
            </ModalBody>
          </Modal>
        </div>
        )
  }
}


const validate = values => {
  const errors = {};

  if(!values.company) {
    errors.company = '请选择报销公司';
  }
  return errors
};

const selector = formValueSelector('InvoiceListForm') ;
const reduxInvoiceListForm = reduxForm({
  form: 'InvoiceListForm',
  validate
})(InvoiceList);

function mapStateToProps(state) {
  return {
    selectedCompany:selector(state, 'chain'),
    all: SelectedInvoicesSelector(state),
    selectedIds: state.invoice.selectedIds
  };
}

export default connect(mapStateToProps, { reimburseAction })(reduxInvoiceListForm);