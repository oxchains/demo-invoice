/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 21/04/2017
 *
 */

import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { fetchInvoiceList, selectInvoice, deselectInvoice, clearReimburseResult } from '../actions/invoice';
import { Link } from 'react-router';
import Moment from 'react-moment';
import _ from 'lodash';
import {
  Modal,
  ModalHeader,
  ModalTitle,
  ModalClose,
  ModalBody,
  ModalFooter
} from 'react-modal-bootstrap';
import SelectedInvoiceList from './selected_invoice_list';
import InvoiceTransfer from './invoice_transfer';

class InvoiceList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      biz : JSON.parse(localStorage.getItem('biz')),
      isModalOpen: false,
      selectedItem: null
    };
  }

  componentWillMount() {
    //this.props.fetchInvoiceList(this.props.params.page);
    this.props.fetchInvoiceList(1);
  }

  componentWillReceiveProps(nextProps) {
    if(nextProps.reimburseResult) {
      this.props.clearReimburseResult();
      this.props.fetchInvoiceList(1);
    }
  }

  handleSelect({ serial }, event) {
    const { selectInvoice, deselectInvoice } = this.props;

    event.target.checked ? selectInvoice(serial) : deselectInvoice(serial);
  }

  hideModal() {
    this.setState({isModalOpen: false});
  }

  renderRows() {
    return this.props.all.map((row, idx) => {
      return (<tr key={idx}>
        <td>{row.serial}</td>
        <td><Moment locale="zh-cn" format="lll">{row.createtime}</Moment></td>
        <td>{row.goods.map(g=>{return g.name}).join()}</td>
        <td>{row.origin.name}</td>
        <td>{row.target.name}</td>
        <td>
          <Link to={`/invoice/detail/${row.serial}`} className="btn btn-default btn-sm margin-r-5" >详情</Link>
          { row.code==0 && <button className="btn btn-default btn-sm" onClick={this.handleItemClick.bind(this, row)} key={idx}>
              流转</button>
          }
        </td>
        { !this.state.biz && <td><input type="checkbox" label={row.serial} key={row.serial}
                   onChange={this.handleSelect.bind(this, row)} disabled={row.code!=0}
                   checked={_.contains(this.props.selectedIds, row.serial)}/>
        </td>}
      </tr>);
    });
  }

  handlePageClick(data) {
    let selected = data.selected;
    this.props.fetchInvoiceList(selected + 1);
  };

  render() {
    return (
      <div className="row">
        <div className="col-xs-12">
          <div className="box box-info">
            <div className="box-header"><h3 className="box-title">发票列表</h3></div>
            <div className="box-body table-responsive no-padding">
              <table className="table table-bordered table-hover">
                <tbody>
                <tr>
                  <th>发票编号</th>
                  <th>开票时间</th>
                  <th>商品</th>
                  <th>开票方</th>
                  <th>抬头</th>
                  <th>操作</th>
                  { !this.state.biz && <th>选择</th> }
                </tr>
                { this.renderRows() }
                </tbody>
              </table>
            </div>
            <div className="box-footer clearfix hidden">
              <button className="btn btn-success pull-right hide">报销</button>
            </div>
          </div>
        </div>

        <Modal isOpen={this.state.isTransferModalOpen} onRequestHide={this.hideExecuteModal.bind(this)}>
          <ModalHeader>
            <ModalClose onClick={this.hideExecuteModal.bind(this)}/>
            <ModalTitle>发票流转</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <InvoiceTransfer actionCallback={this.transferCallback.bind(this)} selectedItem={this.state.selectedItem}/>
          </ModalBody>
          <ModalFooter>
          </ModalFooter>
        </Modal>

        {/* Result Modal */}
        <Modal isOpen={this.state.isModalOpen} onRequestHide={this.hideModal.bind(this)}>
          <ModalHeader>
            <ModalClose onClick={this.hideModal.bind(this)}/>
            <ModalTitle>结果</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <p className={this.state.actionSuccess?'text-green':'text-red'}>
              {this.state.actionResult}
            </p>
          </ModalBody>
          <ModalFooter>
          </ModalFooter>
        </Modal>

      </div>)
  }


  handleItemClick(row) {
    this.setState({isTransferModalOpen: true, selectedItem: row});
  }

  hideExecuteModal = () => {
    this.setState({ isTransferModalOpen : false, selectedItem: null });
  };

  transferCallback(err) {
    if(!err){
      this.props.fetchInvoiceList(1);
      this.setState({isTransferModalOpen : false, isModalOpen: true ,actionSuccess:true, actionResult:'发票流转成功!' });
    }
  }
}

function mapStateToProps(state) {
  return {
    all: state.invoice.all,
    reimburseResult: state.invoice.reimburseResult,
    selectedIds: state.invoice.selectedIds,
    pageCount: state.invoice.pageCount
  };
}

export default connect(mapStateToProps, { fetchInvoiceList, selectInvoice, deselectInvoice, clearReimburseResult })(InvoiceList);