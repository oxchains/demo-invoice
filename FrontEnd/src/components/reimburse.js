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
import { fetchReimburse, reimburseAction } from '../actions/reimburse';
import { Link, browserHistory } from 'react-router';
import Moment from 'react-moment';
import {
  Modal,
  ModalHeader,
  ModalTitle,
  ModalClose,
  ModalBody,
  ModalFooter
} from 'react-modal-bootstrap';

class Reimburse extends Component {
  constructor(props) {
    super(props);
    this.state = {
      biz          : JSON.parse(localStorage.getItem('biz')),
      isModalOpen  : false,
      spinReject   : false,
      spinConfirm  : false,
      error        : null,
      actionResult : ''
    }
  }

  hideModal = () => {
    this.setState({
      isModalOpen: false
    });
  };

  componentWillMount() {
    this.props.fetchReimburse(this.props.params.serial);
  }

  render() {
    const { item } = this.props;

    if(!item) {
      return <div>Loading...</div>;
    }

    return (
      <div className="row">
        <div className="col-lg-8 col-lg-offset-2 col-xs-12">
          <div className="box box-info">
            <div className="box-header  with-border text-center"><h3 className="box-title">报销详情</h3></div>
            <div className="box-body">
              <div className="panel panel-default">
                <div className="panel-heading">报销信息</div>
                <div className="panel-body">
                  <dl className="dl-horizontal">
                    <dl className="dl-horizontal">
                      <dt>报销编号</dt>
                      <dd>{item.serial}</dd>
                      <dt>报销人</dt>
                      <dd>{item.customer}</dd>
                      <dt>单位</dt>
                      <dd>{item.company.name}</dd>
                      <dt>报销状态</dt>
                      <dd>{item.status}</dd>
                      <dt>报销时间</dt>
                      <dd><Moment locale="zh-cn" format="lll">{item.date}</Moment></dd>
                      <dt>说明</dt>
                      <dd>{item.description}</dd>
                    </dl>
                  </dl>
                </div>
              </div>

              { item.invoices.map((ivc,idx)=>{
                return <div className="panel panel-default">
                  <div className="panel-heading">发票{idx+1}</div>
                  <div className="panel-body">
                    <dl className="dl-horizontal">
                      <dl className="dl-horizontal">
                        <dt>编号</dt>
                        <dd>{ivc.serial}</dd>
                        <dt>Owner</dt>
                        <dd>{ivc.owner}</dd>
                        <dt>状态</dt>
                        <dd>{ivc.status}</dd>
                        <dt>开票时间</dt>
                        <dd>{ivc.createtime}</dd>
                        <dt>流转历史</dt>
                        <dd>{ivc.history.join(', ')}</dd>
                        <dt>开票方</dt>
                        <dd>{ivc.origin.name}</dd>
                        <dt>抬头</dt>
                        <dd>{ivc.target.name}</dd>
                      </dl>
                    </dl>

                    {ivc.goods.map((g,idxx)=>{
                      return <div className="panel panel-default">
                        <div className="panel-heading">商品{idxx+1}</div>
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

                  </div>
                </div>
              }) }

            </div>
            <div className="box-footer text-center clearfix">
              <button className="btn btn-default margin-r-5" onClick={browserHistory.goBack}>返回</button>
              { this.state.biz && <span>
                  <button className="btn btn-danger margin-r-5" onClick={this.handleReject.bind(this)} disabled={item.code!=0}>
                    <i className={`fa fa-spinner fa-spin ${this.state.spinReject?'':'hidden'}`}></i> 拒绝报销
                  </button>
                  <button className="btn btn-success" onClick={this.handleConfirm.bind(this)} disabled={item.code!=0}>
                    <i className={`fa fa-spinner fa-spin ${this.state.spinConfirm?'':'hidden'}`}></i> 确认报销
                  </button>
                </span>
              }
            </div>
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

      </div>)
  }

  handleConfirm() {
    this.setState({ spinConfirm:true });
    this.props.reimburseAction(this.props.item.serial, 1, null, err=>{
      this.setState({ isModalOpen: true , error: err , actionResult: err||'确认成功!' , spinConfirm:false });
      this.props.fetchReimburse(this.props.params.serial);
    });
  }

  handleReject() {
    this.setState({ spinReject:true });
    this.props.reimburseAction(this.props.item.serial, 0, null, err=>{
      this.setState({ isModalOpen: true , error: err , actionResult: err||'拒绝成功!' , spinReject:false });
      this.props.fetchReimburse(this.props.params.serial);
    });
  }
}

function mapStateToProps(state) {
  return {
    item: state.reimburse.item
  };
}

export default connect(mapStateToProps, { fetchReimburse, reimburseAction })(Reimburse);