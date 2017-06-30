/**
 * oxchain
 *
 *
 * Author: Jun
 * Date: 30/06/2017
 *
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { fetchCompanyList } from '../actions/company';


class CompanySelector extends React.Component {

  componentWillMount() {
    this.props.fetchCompanyList();
  }

  renderSelectOptions() {
    if(!this.props.companies) return null;
    return this.props.companies.map((row, idx) => {
      return <option value={row.name} key={idx}>{row.name}</option>
    });
  }

  render() {
    const { input, label, vertical, required , meta: { touched, error } } = this.props;
    return (
      <div className={`form-group has-feedback ${touched && error ? 'has-error' : ''}`}>
        <label className={`${vertical?'col-sm-12':'col-sm-3'} control-label`}><b className="text-danger">{required?'*':''}</b> {label}</label>
        <div className={`${vertical?'col-sm-12':'col-sm-9'} input-group`}>
          <select {...input} className="form-control">
            <option></option>
            {this.renderSelectOptions()}
          </select>
          <div className="help-block with-errors">{touched && error ? error : ''}</div>
        </div>
      </div>
    );
  }
}


function mapStateToProps(state) {
  return {
    companies: state.company.all
  };
}

export default connect(mapStateToProps, { fetchCompanyList })(CompanySelector);