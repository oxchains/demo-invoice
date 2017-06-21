/**
 * oxchain
 *
 *
 * Author: Jun
 * Date: 14/06/2017
 *
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import {
  Modal,
  ModalHeader,
  ModalTitle,
  ModalClose,
  ModalBody,
  ModalFooter
} from 'react-modal-bootstrap';
import BillRegister from './bill_actions/bill_register';
import PromptAcceptance from './bill_actions/prompt_acceptance';
import PromptWarrant from './bill_actions/prompt_warrant';
import PromptRevoke from './bill_actions/prompt_revoke';
import PromptReceive from './bill_actions/prompt_receive';
import PromptEndorsement from './bill_actions/prompt_endorsement';
import PromptDiscount from './bill_actions/prompt_discount';
import PromptPledge from './bill_actions/prompt_pledge';
import PromptPledgeRelease from './bill_actions/prompt_pledge_release';
import PromptPay from './bill_actions/prompt_pay';
import PromptDun from './bill_actions/prompt_dun';

class BillActions extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isAlertModalOpen : false,
      alertSuccess: true,
      alert: null,
      isRegisterModalOpen : false
    };

  }

  componentWillMount() {
  }

  hideAlertModal = () => {
    this.setState({
      isAlertModalOpen: false
    });
  };

  handleAddClick(key) {
    this.setState({[key]: true});
  }

  hideAddModal = (key) => {
    this.setState({ [key] : false });
  };

  addCallback(key,err) {
    console.log(key, err);
    if(!err){
      this.setState({[key] : false, isAlertModalOpen: true ,alertSuccess:true, alert:'操作成功!' });
    }
  }

  render() {

    const actions = [
      {'title':'出票登记', 'displayKey':'isRegisterModalOpen'},
      {'title':'提示承兑/撤销/确认', 'displayKey':'isPromptAcceptanceOpen'},
      {'title':'提示保证/撤销/确认', 'displayKey':'isPromptWarrantOpen'},
      {'title':'出票人撤票', 'displayKey':'isPromptRevokeOpen'},
      {'title':'提示收票/撤销/确认', 'displayKey':'isPromptReceiveOpen'},
      {'title':'提示背书/撤销/确认', 'displayKey':'isPromptEndorsementOpen'},
      {'title':'提示贴现/撤销/确认', 'displayKey':'isPromptDiscountOpen'},
      {'title':'提示质押/撤销/确认', 'displayKey':'isPromptPledgeOpen'},
      {'title':'提示质押解除/撤销/确认', 'displayKey':'isPromptPledgeReleaseOpen'},
      {'title':'提示付款/撤销/确认/拒绝', 'displayKey':'isPromptPayOpen'},
      {'title':'提示追索/撤销', 'displayKey':'isPromptDunOpen'},
    ];

    return (
      <div>
        <section className="content-header"><h1></h1></section>
        <section className="content">
          <div className="row">
            <div className="col-xs-12">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">票据操作功能列表</h3>

                </div>
                <div className="box-body table-responsive">
                  <div className="list-group">
                    {actions.map((row, idx) => {
                      return <button type="button" className="list-group-item list-group-item-action" key={idx}
                              onClick={this.handleAddClick.bind(this, row.displayKey)}>{row.title}</button>
                    })}
                  </div>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Alert Modal */}
        <Modal isOpen={this.state.isAlertModalOpen} onRequestHide={this.hideAlertModal.bind(this)}>
          <ModalHeader>
            <ModalClose onClick={this.hideAlertModal.bind(this)}/>
            <ModalTitle>提示</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <p className={this.state.alertSuccess?'text-green':'text-red'}>
              {this.state.alert}
            </p>
          </ModalBody>
          <ModalFooter>
          </ModalFooter>
        </Modal>

        <Modal isOpen={this.state.isRegisterModalOpen} onRequestHide={this.hideAddModal.bind(this, 'isRegisterModalOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isRegisterModalOpen')}/>
            <ModalTitle>出票登记</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <BillRegister addCallback={this.addCallback.bind(this, 'isRegisterModalOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptAcceptanceOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptAcceptanceOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptAcceptanceOpen')}/>
            <ModalTitle>提示承兑/撤销/确认</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptAcceptance addCallback={this.addCallback.bind(this, 'isPromptAcceptanceOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptWarrantOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptWarrantOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptWarrantOpen')}/>
            <ModalTitle>提示保证/撤销/确认</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptWarrant addCallback={this.addCallback.bind(this, 'isPromptWarrantOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptRevokeOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptRevokeOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptRevokeOpen')}/>
            <ModalTitle>出票人撤票</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptRevoke addCallback={this.addCallback.bind(this, 'isPromptRevokeOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptReceiveOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptReceiveOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptReceiveOpen')}/>
            <ModalTitle>出票人收票/撤销/确认</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptReceive addCallback={this.addCallback.bind(this, 'isPromptReceiveOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptEndorsementOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptEndorsementOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptEndorsementOpen')}/>
            <ModalTitle>提示背书/撤销/确认</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptEndorsement addCallback={this.addCallback.bind(this, 'isPromptEndorsementOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptDiscountOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptDiscountOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptDiscountOpen')}/>
            <ModalTitle>提示贴现/撤销/确认</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptDiscount addCallback={this.addCallback.bind(this, 'isPromptDiscountOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptPledgeOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptPledgeOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptPledgeOpen')}/>
            <ModalTitle>提示质押/撤销/确认</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptPledge addCallback={this.addCallback.bind(this, 'isPromptPledgeOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptPledgeReleaseOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptPledgeReleaseOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptPledgeReleaseOpen')}/>
            <ModalTitle>提示质押解除/撤销/确认</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptPledgeRelease addCallback={this.addCallback.bind(this, 'isPromptPledgeReleaseOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptPayOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptPayOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptPayOpen')}/>
            <ModalTitle>提示付款/撤销/确认/拒绝</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptPay addCallback={this.addCallback.bind(this, 'isPromptPayOpen')}/>
          </ModalBody>
        </Modal>

        <Modal isOpen={this.state.isPromptDunOpen} onRequestHide={this.hideAddModal.bind(this, 'isPromptDunOpen')}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal.bind(this, 'isPromptDunOpen')}/>
            <ModalTitle>提示追索/撤销</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <PromptDun addCallback={this.addCallback.bind(this, 'isPromptDunOpen')}/>
          </ModalBody>
        </Modal>

      </div>)
  }
}

export default BillActions;