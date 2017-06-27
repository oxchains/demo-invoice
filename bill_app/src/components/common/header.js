import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';

const logo = 'https://www.oxchains.com/images/logo-black.png';

class Header extends  Component {

  renderUserInfo() {
    if(this.props.authenticated) {
      const user = JSON.parse(localStorage.getItem('user'));
      const username= user.username;
      const avatar = `https://gravatar.com/avatar/oxchain-${username}?s=100&d=retro`;

      return (
        <div className="navbar-custom-menu">
        <ul className="nav navbar-nav">
          <li className="dropdown user user-menu">
            <Link to="#" className="dropdown-toggle" data-toggle="dropdown">
              <img className="user-image" alt="User Image" src={avatar}/>
              <span className="hidden-xs">{username}</span>
            </Link>
            <ul className="dropdown-menu">
              <li className="user-header">
                <img className="img-circle" alt="User Image" src={avatar}/>

                <p>
                  {username}
                  <small></small>
                </p>
              </li>
              <li className="user-body">
                <div className="row">
                </div>
              </li>
              <li className="user-footer">
                <div className="pull-left">
                  <Link to="#" className="btn btn-default btn-flat">个人设置</Link>
                </div>
                <div className="pull-right">
                  <Link to="/signout" className="btn btn-default btn-flat">退出登录</Link>
                </div>
              </li>
            </ul>
          </li>
        </ul>
      </div>);
    } else {
      return <div></div>
    }
  }

  render() {

    return (
      <header className="main-header">
        <Link to="/" className="logo">
          <span className="logo-mini"><img src={logo} style={{width:50+'px'}} /></span>
          <span className="logo-lg"><img src={logo} style={{width:80+'px'}} /><b>OXCHAIN</b></span>
        </Link>
        <nav className="navbar navbar-static-top">
          <Link to="#" className="sidebar-toggle" data-toggle="offcanvas" role="button">
            <span className="sr-only">Toggle navigation</span>
          </Link>

          {this.renderUserInfo()}

        </nav>
      </header>
    );
  }

}

function mapStateToProps(state) {
  return {
    authenticated: state.auth.authenticated
  };
}

export default connect(mapStateToProps)(Header);