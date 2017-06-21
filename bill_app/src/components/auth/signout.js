import React, { Component } from 'react';
import { connect } from 'react-redux';
import { signoutUser } from '../../actions/auth';

class Signout extends Component {
  componentWillMount() {
    this.props.signoutUser();
  }

  render() {
    return (
      <div>
        <section className="content-header"><h1></h1></section>
        <section className="content">
          <div className="text-center"><h2>您已退出登录</h2></div>
        </section>
      </div>);
  }
}

export default connect(null, { signoutUser })(Signout);
