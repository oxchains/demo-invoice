import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';

class NavSide extends  Component {
  constructor(props) {
    super(props);
    this.state = {current:null}

    this.renderLink = this.renderLink.bind(this);
  }

  renderUserInfo() {
    if(this.props.authenticated) {
      const user = JSON.parse(localStorage.getItem('user'));
      const username= user.username;
      const avatar = `https://gravatar.com/avatar/oxchain-${username}?s=100&d=retro`;
      return <div className="user-panel">
        <div className="pull-left image">
          <img src={avatar} className="img-circle" alt="User Image" style={{"width":"100px"}} />
        </div>
        <div className="pull-left info">
          <p>{username}</p>
        </div>
      </div>
    } else {
      return <div></div>
    }
  }

  handleLinkClick(e) {
    //console.log(e.target)
    this.setState({current: e.target.id });
  }

  renderLoginLinks() {
    if (this.props.authenticated) {
      // show a link to sign out
      return  <li key="nav1"><Link to="/signout"><i className="fa fa-sign-in"></i> <span>退出登录</span></Link></li>
    } else {
      return [
        <li key="nav2"><Link to="/signin"><i className="fa fa-sign-in"></i> <span>登录</span></Link></li>,
      ];
    }
  }

  renderLink({path, title, icon}) {
    return (<li key={path} className={this.state.current==path?'active':''}
                onClick={this.handleLinkClick.bind(this)}>
      <Link id={path} to={'/'+path}><i className={`fa fa-${icon}`}></i> <span>{title}</span></Link>
    </li>)
  }

  render() {
    const links = [
      {path:'bill/actions', title:'票据操作', icon:'bitcoin'},
      {path:'bills', title:'我的票据', icon:'bitcoin'},
      {path:'users', title:'用户管理', icon:'users'}
    ];

    return (
      <aside className="main-sidebar">
        <section className="sidebar">
          { this.renderUserInfo() }
          <ul className="sidebar-menu">
            <li className="header">导航</li>
            { this.renderLoginLinks() }
            { links.map(this.renderLink) }
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