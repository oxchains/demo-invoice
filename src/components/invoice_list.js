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
import { fetchInvoiceList } from '../actions/invoice';
import { Link } from 'react-router';
import Moment from 'react-moment';
import ReactPaginate from 'react-paginate';

class InvoiceList extends Component {

  componentWillMount() {
    this.props.fetchInvoiceList(this.props.params.page);
  }

  renderRows() {
    return this.props.all.map((row, idx) => {
      return (<tr key={idx}>
        <td>{idx+1}</td>
        <td>{row.id}</td>
        <td>{row.history}</td>
        <td>{row.companyTitle}</td>
        <td>{row.origin}</td>
        <td><Moment locale="zh-cn" format="lll">{row.date}</Moment></td>
        <td>{row.state}</td>
        <td><input type="checkbox"></input></td>
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
                  <th>序号</th>
                  <th>发票编号</th>
                  <th>交易历史</th>
                  <th>公司名称</th>
                  <th>来源</th>
                  <th>开票时间</th>
                  <th>状态</th>
                  <th>选择</th>
                </tr>
                { this.renderRows() }
                </tbody>
              </table>
            </div>
            <div className="box-footer clearfix">
              <ReactPaginate previousLabel={"«"}
                             nextLabel={"»"}
                             breakLabel={<a href="">...</a>}
                             breakClassName={"break-me"}
                             pageCount={this.props.pageCount}
                             initialPage={this.props.params.page-1}
                             disableInitialCallback={true}
                             marginPagesDisplayed={2}
                             pageRangeDisplayed={5}
                             onPageChange={this.handlePageClick.bind(this)}
                             containerClassName={"pagination pagination-sm no-margin "}
                             subContainerClassName={"pages pagination"}
                             activeClassName={"active"} />
              <button className="btn btn-success pull-right">报销</button>
            </div>
          </div>
        </div>
      </div>)
  }
}

function mapStateToProps(state) {
  return {
    all: state.invoice.all,
    pageCount: state.invoice.pageCount
  };
}

export default connect(mapStateToProps, { fetchInvoiceList })(InvoiceList);