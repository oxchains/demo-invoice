/**
 * oxchain
 *
 *
 * Author: Jun
 * Date: 29/06/2017
 *
 */

import React, { Component } from 'react';
import InvoiceList from './invoice_list';
import SelectedInvoiceList from './selected_invoice_list';

export default class InvoicesReimburse extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div className="row">
        <div className="col-xs-7">
          <InvoiceList/>
        </div>
        <div className="col-xs-5">
          <SelectedInvoiceList/>
        </div>

      </div>)
  }
}
