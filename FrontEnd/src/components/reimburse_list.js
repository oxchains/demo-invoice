/**
 * oxchain ivoice app
 *
 * 企业发票审核列表
 *
 * Author: Jun
 * Email: iyakexi@gmail.com
 * Date: 20/04/2017
 *
 */

import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { fetchReimburseList } from '../actions/reimburse';
import { Link } from 'react-router';
import Moment from 'react-moment';
import ReactPaginate from 'react-paginate';

class ReimburseList extends Component {

  componentWillMount() {
    this.props.fetchReimburseList(this.props.params.page);
  }

  renderRows() {
    return this.props.all.map((row, idx) => {
      return (<tr key={idx}>
        <td>{idx+1}</td>
        <td>{row.serial}</td>
        <td><Moment locale="zh-cn" format="lll">{row.createtime}</Moment></td>
        <td>{row.status}</td>
        <td>{row.customer}</td>
        <td>{row.description}</td>
        <td>
          <Link to={`/reimburse/${row.serial}`} >详情</Link>
        </td>
      </tr>);
    });
  }

  handlePageClick(data) {
    let selected = data.selected;
    this.props.fetchReimburseList(selected + 1);
  };

  render() {
    return (
      <div className="row">
        <div className="col-xs-12">
          <div className="box box-info">
            <div className="box-header"><h3 className="box-title">报销列表</h3></div>
            <div className="box-body table-responsive no-padding">
              <table className="table table-bordered table-hover">
                <tbody>
                <tr>
                  <th>序号</th>
                  <th>报销编号</th>
                  <th>报销时间</th>
                  <th>状态</th>
                  <th>报销人</th>
                  <th>描述</th>
                  <th>操作</th>
                </tr>
                { this.renderRows() }
                </tbody>
              </table>
            </div>
            <div className="box-footer clearfix hidden">
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
            </div>
          </div>
        </div>
      </div>)
  }
}

function mapStateToProps(state) {
  return {
    all: state.reimburse.all,
    pageCount: state.reimburse.pageCount
  };
}

export default connect(mapStateToProps, { fetchReimburseList })(ReimburseList);