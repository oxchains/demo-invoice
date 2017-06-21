import React from 'react';
import Moment from 'react-moment';

export default () => {
  return (
  <div>
    <section className="content-header"><h1></h1></section>
    <section className="content">
      <div className="row">
        <div className="md-col-12 text-center">
          <h1>欢迎使用OXCHAIN汇票系统</h1>
          <div>现在时间是: <Moment locale="zh-cn" format="lll"></Moment></div>
        </div>
      </div>
    </section>
  </div>);
};