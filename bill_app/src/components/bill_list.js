/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 25/05/2017
 *
 */

import React, {Component} from 'react';
import {connect} from 'react-redux';
import {
  fetchAcceptanceList,
  fetchDiscountList,
  fetchEndorsementList,
  fetchGuarantyList,
  fetchPaymentList,
  fetchPledgeList,
  fetchReceptionList,
  fetchRecourseList,
  fetchReleaseList,
  fetchRevocationList
} from '../actions/bill';
import {Link} from 'react-router-dom';
import Moment from 'react-moment';
import {
  Modal,
  ModalHeader,
  ModalTitle,
  ModalClose,
  ModalBody,
  ModalFooter
} from 'react-modal-bootstrap';

class BillList extends Component {
  constructor(props) {
    super(props);
  }

  componentWillMount() {
    const {username} = this.props;
    console.log(username);
    this.props.fetchAcceptanceList(username);
    this.props.fetchDiscountList(username);
    this.props.fetchEndorsementList(username);
    this.props.fetchGuarantyList(username);
    this.props.fetchPaymentList(username);
    this.props.fetchPledgeList(username);
    this.props.fetchReceptionList(username);
    this.props.fetchRecourseList(username);
    this.props.fetchReleaseList(username);
    this.props.fetchRevocationList(username);
  }

  renderAcceptance() {
    return this.props.acceptance.map((row, idx) => {
      return (<tr key={idx}>
        <td>{row.id}</td>
        <td>￥{row.price}</td>
        <td>{row.drawer}</td>
        <td>{row.drawee}</td>
        <td>{row.payee}</td>
        <td>{row.date}</td>
        <td><Moment format="YYYY-MM-DD">{row.due}</Moment></td>
        <td>{row.transferable == 1 ? '是' : '否'}</td>
        <td>{row.status}</td>
      </tr>);
    });
  }

  renderBillList(list) {
    return list.map((item, index) => {
      return (
        <tr key={index}>
          <td>{item.id}</td>
          <td>￥{item.price}</td>
          <td>{item.drawer}</td>
          <td>{item.drawee}</td>
          <td>{item.payee}</td>
          <td>{item.date}</td>
          <td><Moment format="YYYY-MM-DD">{item.due}</Moment></td>
          <td>{item.transferable == 1 ? '是' : '否'}</td>
          <td>{item.status}</td>
        </tr>
      );
    });
  }


  render() {

    if (this.props.acceptance === null) {
      return <div>
        <section className="content"><h3>Loading...</h3></section>
      </div>
    }

    return (
      <div>
        <section className="content-header"><h1></h1></section>
        <section className="content">
          <div className="row">
            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">(待)承兑列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>出票人</th>
                      <th>承兑人</th>
                      <th>付款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderAcceptance() }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>

            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">(待)保证列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>开票人</th>
                      <th>受票人/付款人</th>
                      <th>收款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderBillList(this.props.guaranty) }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>
          </div>
          <div className="row">

            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">撤票列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>开票人</th>
                      <th>受票人/付款人</th>
                      <th>收款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderBillList(this.props.revocation) }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>

            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">(待)收票列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>开票人</th>
                      <th>受票人/付款人</th>
                      <th>收款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderBillList(this.props.reception)}
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>
          </div>
          <div className="row">
            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">(待)背书列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>开票人</th>
                      <th>受票人/付款人</th>
                      <th>收款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderBillList(this.props.endorsement) }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>

            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">(待)贴现列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>开票人</th>
                      <th>受票人/付款人</th>
                      <th>收款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderBillList(this.props.discount) }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>
          </div>
          <div className="row">
            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">(待)质押列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>开票人</th>
                      <th>受票人/付款人</th>
                      <th>收款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderBillList(this.props.pledge) }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>

            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">(待)质押解除列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>开票人</th>
                      <th>受票人/付款人</th>
                      <th>收款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderBillList(this.props.release) }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>
          </div>
          <div className="row">
            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">(待)支付列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>开票人</th>
                      <th>受票人/付款人</th>
                      <th>收款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderBillList(this.props.payment) }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>

            <div className="col-xs-12 col-md-6">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">(待)追索列表</h3>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover multi-line-table">
                    <tbody>
                    <tr>
                      <th>票据号</th>
                      <th>金额</th>
                      <th>开票人</th>
                      <th>受票人/付款人</th>
                      <th>收款人</th>
                      <th>出票日期</th>
                      <th>到期日</th>
                      <th>可转移</th>
                      <th>票据状态</th>
                    </tr>
                    { this.renderBillList(this.props.recourse) }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>
          </div>
        </section>

      </div>)
  }
}

function mapStateToProps(state) {
  return {
    acceptance: state.bill.acceptance,
    guaranty: state.bill.guaranty,
    revocation: state.bill.revocation,
    reception: state.bill.reception,
    endorsement: state.bill.endorsement,
    discount: state.bill.discount,
    pledge: state.bill.pledge,
    release: state.bill.release,
    payment: state.bill.payment,
    recourse: state.bill.recourse,
    username: state.auth.username
  };
}

export default connect(mapStateToProps, {
  fetchAcceptanceList,
  fetchDiscountList,
  fetchEndorsementList,
  fetchGuarantyList,
  fetchPaymentList,
  fetchReceptionList,
  fetchRecourseList,
  fetchReleaseList,
  fetchRevocationList,
  fetchPledgeList
})(BillList);