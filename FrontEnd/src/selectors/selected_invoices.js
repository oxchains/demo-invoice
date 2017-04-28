/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Email: iyakexi@gmail.com
 * Date: 26/04/2017
 *
 */

import _ from 'lodash';
import { createSelector } from 'reselect';

const invoicesSelector = state => state.invoice.all;
const selectedInvoicesSelector = state => state.invoice.selectedIds;

const getInvoices = (invoices, selectedIds) => {
  const selectedInvoices = _.filter(
    invoices,
    invoice => _.contains(selectedIds, invoice.id)
  );
;
  return selectedInvoices;
};

export default createSelector(
  invoicesSelector,
  selectedInvoicesSelector,
  getInvoices
);