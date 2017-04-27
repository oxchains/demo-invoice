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
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import Moment from 'react-moment';
import _ from 'lodash';
import { reimburseAction } from '../actions/invoice';
import SelectedInvoicesSelector from '../selectors/selected_invoices';

class InvoiceList extends Component {

  handleReimburse() {
    console.log(this.props.selectedIds);
    if(this.props.selectedIds.length > 0) {
      this.props.reimburseAction(this.props.selectedIds);
    }
  }

  renderRows() {
    return this.props.all.map((row, idx) => {
      return (<tr key={idx}>
        <td>{row.id}</td>
        <td>{row.history}</td>
        <td>{row.companyTitle}</td>
        <td>{row.origin}</td>
        <td><Moment locale="zh-cn" format="lll">{row.date}</Moment></td>
        <td>{row.state}</td>
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
                <th>交易历史</th>
                <th>公司名称</th>
                <th>来源</th>
                <th>开票时间</th>
                <th>状态</th>
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
        </div>
        )
  }
}

function mapStateToProps(state) {
  return {
    all: SelectedInvoicesSelector(state),
    selectedIds: state.invoice.selectedIds
  };
}

export default connect(mapStateToProps, { reimburseAction })(InvoiceList);