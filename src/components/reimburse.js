/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Email: iyakexi@gmail.com
 * Date: 21/04/2017
 *
 */

import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { fetchReimburse } from '../actions/reimburse';
import { Link, browserHistory } from 'react-router';
import Moment from 'react-moment';

class Reimburse extends Component {

  componentWillMount() {
    this.props.fetchReimburse(this.props.params.id);
  }

  render() {
    const { item } = this.props;

    if(!item) {
      return <div>Loading...</div>;
    }

    return (
      <div className="row">
        <div className="col-xs-6 col-xs-offset-3">
          <div className="box box-info">
            <div className="box-header  with-border text-center"><h3 className="box-title">企业发票审核</h3></div>
            <div className="box-body">
              <dl className="dl-horizontal">
                <dt>发票号</dt>
                <dd>{item.id}</dd>
                <dt>姓名</dt>
                <dd>{item.name}</dd>
                <dt>单位</dt>
                <dd>{item.companyTitle}</dd>
                <dt>部门</dt>
                <dd>{item.department}</dd>
                <dt>发票列表</dt>
                <dd>{item.invoiceNumberList}</dd>
                <dt>说明</dt>
                <dd>{item.description}</dd>
                <dt>报销状态</dt>
                <dd>{item.state}</dd>
                <dt>报销时间</dt>
                <dd><Moment locale="zh-cn" format="lll">{item.date}</Moment></dd>
              </dl>
            </div>
            <div className="box-footer text-center clearfix">
              <button className="btn btn-default margin-r-5" onClick={browserHistory.goBack}>取消</button>
              <button className="btn btn-success">确认报销</button>
            </div>
          </div>
        </div>
      </div>)
  }
}

function mapStateToProps(state) {
  return {
    item: state.reimburse.item
  };
}

export default connect(mapStateToProps, { fetchReimburse })(Reimburse);