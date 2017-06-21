/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 25/05/2017
 *
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { fetchAcceptanceList } from '../actions/bill';
import { Link } from 'react-router-dom';
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
    this.props.fetchAcceptanceList('a');
  }

  renderRows() {
    return this.props.acceptance.map((row, idx) => {
      return (<tr key={idx}>
        <td>{row.id}</td>
        <td>￥{row.price}</td>
        <td>{row.drawer}</td>
        <td>{row.drawee}</td>
        <td>{row.payee}</td>
        <td>{row.date}</td>
        <td><Moment format="YYYY-MM-DD">{row.due}</Moment></td>
        <td>{row.transferable==1?'是':'否'}</td>
        <td>
        </td>
      </tr>);
    });
  }


  render() {

    if(this.props.acceptance===null) {
      return <div><section className="content"><h3>Loading...</h3></section></div>
    }

    return (
      <div>
        <section className="content-header"><h1></h1></section>
        <section className="content">
          <div className="row">
            <div className="col-xs-6">
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
                      <th>操作</th>
                    </tr>
                    { this.renderRows() }
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
    acceptance: state.bill.acceptance
  };
}

export default connect(mapStateToProps, { fetchAcceptanceList })(BillList);