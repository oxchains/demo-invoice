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
        <td>{row.id}</td>
        <td>{row.department}</td>
        <td>{row.name}</td>
        <td><Moment locale="zh-cn" format="lll">{row.date}</Moment></td>
        <td>{row.state}</td>
        <td><Link className="btn btn-sm btn-default" to={`/reimburse/${idx+1}`}>处理</Link></td>
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
            <div className="box-header"><h3 className="box-title">企业发票审核</h3></div>
            <div className="box-body table-responsive no-padding">
              <table className="table table-bordered table-hover">
                <tbody>
                <tr>
                  <th>序号</th>
                  <th>报销编号</th>
                  <th>部门</th>
                  <th>姓名</th>
                  <th>日期</th>
                  <th>状态</th>
                  <th>操作</th>
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