/**
 * oxchain
 *
 *
 * Author: Jun
 * Date: 29/06/2017
 *
 */

import React, { Component } from 'react';
import { Field, reduxForm } from 'redux-form';
import { connect } from 'react-redux';
import { fetchInvoiceDetail } from '../actions/invoice';
import { Link } from 'react-router';
import Moment from 'react-moment';
import _ from 'lodash';
import ReactPaginate from 'react-paginate';

class InvoiceDetail extends Component {
  constructor(props) {
    super(props);
  }

  componentWillMount() {
      this.props.fetchInvoiceDetail(this.props.params.serial);
  }

  render() {
    const detail = this.props.detail;
    if(!detail) return <div>Loading...</div>

    return (
      <div className="row">
        <div className="col-xs-12">
          <div className="box box-info">
            <div className="box-header"><h3 className="box-title">发票详情</h3></div>
            <div className="box-body table-responsive">

              <div className="panel panel-default">
                <div className="panel-heading">基本信息</div>
                <div className="panel-body">
                  <dl className="dl-horizontal">
                    <dt>编号</dt>
                    <dd>{detail.serial}</dd>
                    <dt>Owner</dt>
                    <dd>{detail.owner}</dd>
                    <dt>状态</dt>
                    <dd>{detail.status}</dd>
                    <dt>开票时间</dt>
                    <dd>{detail.createtime}</dd>
                    <dt>流转历史</dt>
                    <dd>{detail.history.join(', ')}</dd>
                  </dl>
                </div>
              </div>

              {detail.goods.map(g=>{
                return <div className="panel panel-default">
                  <div className="panel-heading">商品信息</div>
                  <div className="panel-body">
                     <dl className="dl-horizontal">
                      <dt>商品名称</dt>
                      <dd>{g.name}</dd>
                      <dt>金额</dt>
                      <dd>{g.price}</dd>
                      <dt>数量</dt>
                      <dd>{g.quantity}</dd>
                      <dt>描述</dt>
                      <dd>{g.description}</dd>
                    </dl>
                  </div>
                </div>
              })}

              <div className="panel panel-default">
                <div className="panel-heading">开票方</div>
                <div className="panel-body">
                  <dl className="dl-horizontal">
                    <dt>名称</dt>
                    <dd>{detail.origin.name}</dd>
                    <dt>地址</dt>
                    <dd>{detail.origin.address}</dd>
                    <dt>纳税人识别号</dt>
                    <dd>{detail.origin.taxpayer}</dd>
                    <dt>开户行</dt>
                    <dd>{detail.origin.bank}</dd>
                    <dt>账号</dt>
                    <dd>{detail.origin.account}</dd>
                  </dl>
                </div>
              </div>

              <div className="panel panel-default">
                <div className="panel-heading">抬头</div>
                <div className="panel-body">
                  <dl className="dl-horizontal">
                    <dt>名称</dt>
                    <dd>{detail.target.name}</dd>
                    <dt>地址</dt>
                    <dd>{detail.target.address}</dd>
                    <dt>纳税人识别号</dt>
                    <dd>{detail.target.taxpayer}</dd>
                    <dt>开户行</dt>
                    <dd>{detail.target.bank}</dd>
                    <dt>账号</dt>
                    <dd>{detail.target.account}</dd>
                  </dl>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>)
  }
}

function mapStateToProps(state) {
  return {
    detail: state.invoice.item
  };
}

export default connect(mapStateToProps, { fetchInvoiceDetail })(InvoiceDetail);