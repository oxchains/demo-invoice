/**
 * oxchain ivoice app
 *
 *
 * Author: Jun
 * Date: 13/04/2017
 *
 */

import React from 'react';
import Moment from 'react-moment';

export default () => {
  return (
    <div className="row">
      <div className="md-col-12 text-center">
        <h1>欢迎登录牛链电子发票系统</h1>
        <div>现在时间是: <Moment locale="zh-cn" format="lll"></Moment></div>
      </div>
    </div>);
};