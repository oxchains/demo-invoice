/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 13/04/2017
 *
 */
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';

class NavSide extends  Component {

  renderLinks() {
    if (this.props.authenticated) {
      const biz= JSON.parse(localStorage.getItem('biz'));
      if(biz) {
        return [
          <li key="nav5"><Link href="/invoiceAdd"><i className="fa fa-ticket"></i> <span>自动开票</span></Link></li>,
          <li key="nav6"><Link href="/reimburse/list/1"><i className="fa fa-tasks"></i> <span>审核发票</span></Link></li>,
          <li key="nav7"><Link href="/invoice/list/1"><i className="fa fa-list-alt"></i> <span>我的发票</span></Link></li>
        ];
      } else {
        return [
          <li key="nav7"><Link href="/invoices-reimburse"><i className="fa fa-list-alt"></i> <span>我的发票</span></Link></li>,
          <li key="nav8"><Link href="/reimburse/list/1"><i className="fa fa-list"></i> <span>我的报销</span></Link></li>
        ];
      }
    } else {
      return [
        <li key="nav2"><Link href="/signin"><i className="fa fa-sign-in"></i> <span>登录</span></Link></li>,
        <li key="nav3"><Link href="/signup"><i className="fa fa-wpforms"></i> <span>个人用户注册</span></Link></li>,
        <li key="nav4"><Link href="/signupCompany"><i className="fa fa-registered"></i> <span>企业申请注册</span></Link></li>,
        <li key="nav5"><Link href="/invoiceAdd"><i className="fa fa-ticket"></i> <span>自动开票</span></Link></li>,
        <li key="nav6"><Link href="/reimburse/list/1"><i className="fa fa-tasks"></i> <span>企业发票审核</span></Link></li>,
        <li key="nav7"><Link href="/invoice/list/1"><i className="fa fa-list-alt"></i> <span>个人发票列表</span></Link></li>,
        <li key="nav8"><Link href="/reimburse/list/1"><i className="fa fa-list"></i> <span>个人报销列表</span></Link></li>
      ];
    }
  }

  renderUserInfo() {
    if(this.props.authenticated) {
      const username= localStorage.getItem('username');
      const avatar = `https://gravatar.com/avatar/oxchain.org/user/${username}?s=100&d=retro`;
      return <div className="user-panel">
        <div className="pull-left image">
          <img src={avatar} className="img-circle" alt="User Image" style={{"width":"100px"}} />
        </div>
        <div className="pull-left info">
          <p>{username}</p>
          <Link></Link>
        </div>
      </div>
    } else {
      return <div></div>
    }
  }

  render() {
    return (
      <aside className="main-sidebar">
        <section className="sidebar">
          { this.renderUserInfo() }
          <ul className="sidebar-menu">
            <li className="header">导航</li>
            { this.renderLinks() }
          </ul>
        </section>
      </aside>
    );
  }
}

function mapStateToProps(state) {
  return { authenticated: state.auth.authenticated };
}

export default connect(mapStateToProps)(NavSide);