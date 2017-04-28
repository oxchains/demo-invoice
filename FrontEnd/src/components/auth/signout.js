/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 19/04/2017
 *
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';
import { signoutUser } from '../../actions/auth';

class Signout extends Component {
  componentWillMount() {
    this.props.signoutUser();
  }

  render() {
    return <div className="text-center"><h2>您已退出登录</h2></div>;
  }
}

export default connect(null, { signoutUser })(Signout);
