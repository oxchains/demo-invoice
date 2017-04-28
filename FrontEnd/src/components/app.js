import React, { Component } from 'react';
import NavTop from './common/header';
import NavSide from './common/nav_side';
import Footer from './common/footer';

export default class App extends Component {
  render() {
    const pageTitle = '';
    return (
      <div>
        <NavTop />
        <NavSide/>
        <div className="content-wrapper">
          <section className="content-header"><h1>{pageTitle}</h1></section>
          <section className="content">
            {this.props.children}
          </section>
        </div>
        <Footer/>
      </div>
    );
  }
}
