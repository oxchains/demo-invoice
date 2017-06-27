import React, { Component } from 'react';
import { connect } from 'react-redux';
import { fetchUserList, disableUser } from '../actions/user';
import { Link } from 'react-router-dom';
import Moment from 'react-moment';
import {
  Modal,
  ModalHeader,
  ModalTitle,
  ModalClose,
  ModalBody,
  ModalFooter
} from 'react-modal-bootstrap';
import AddUser from './user_add';

class UserList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isModalOpen: false,
      isAddModalOpen : false,
      actionSuccess: null,
      actionResult: ''
    };

  }

  componentWillMount() {

    this.props.fetchUserList();
  }

  hideModal = () => {
    this.setState({
      isModalOpen: false
    });
  };

  renderRows() {
    return this.props.all.map((row, idx) => {
      return (<tr key={idx}>
        <td>{row.username}</td>
        <td>{row.affiliation}</td>
        <td>
          <button className="btn btn-sm btn-danger margin-r-5"
                  onClick={this.handleStopClick.bind(this, row)}>禁用</button>
        </td>
      </tr>);
    });
  }

  handleStopClick({username}) {
    if(!window.confirm('确定禁用该账号？')) return false;
    this.props.disableUser(username, 0 , success => {
      console.log(success);
      if(success) {//操作成功
        this.props.fetchUserList();
      }
      this.setState({ isModalOpen: true ,actionSuccess:success, actionResult: success?'操作成功!':'操作失败' });
    });
  };

  handleAddClick() {
    this.setState({isAddModalOpen: true});
  }

  hideAddModal = () => {
    this.setState({ isAddModalOpen : false });
  };

  addUserCallback(err) {
    if(!err){
      this.props.fetchUserList();
      this.setState({isAddModalOpen : false, isModalOpen: true ,actionSuccess:true, actionResult:'用户注册成功!' });
    }
  }

  render() {
    if(this.props.all===null) {
      return <div><section className="content"><h1>Loading...</h1></section></div>
    }

    return (
      <div>
        <section className="content-header"><h1></h1></section>
        <section className="content">
          <div className="row">
            <div className="col-xs-12">
              <div className="box box-info">
                <div className="box-header">
                  <h3 className="box-title">用户</h3>
                  <button className="btn btn-success pull-right" onClick={this.handleAddClick.bind(this)}><i className="fa fa-plus"></i> 注册用户</button>
                </div>
                <div className="box-body table-responsive no-padding">
                  <table className="table table-bordered table-hover">
                    <tbody>
                    <tr>
                      <th>ID</th>
                      <th>从属</th>
                      <th>操作</th>
                    </tr>
                    { this.renderRows() }
                    </tbody>
                  </table>
                </div>
                <div className="box-footer clearfix">
                </div>
              </div>
            </div>
          </div>
        </section>

        <Modal isOpen={this.state.isModalOpen} onRequestHide={this.hideModal}>
          <ModalHeader>
            <ModalClose onClick={this.hideModal}/>
            <ModalTitle>提示:</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <p className={this.state.actionSuccess?'text-green':'text-red'}>
              {this.state.actionResult}
            </p>
          </ModalBody>
          <ModalFooter>
            <button className='btn btn-default' onClick={this.hideModal}>
              关闭
            </button>
          </ModalFooter>
        </Modal>

        <Modal isOpen={this.state.isAddModalOpen} onRequestHide={this.hideAddModal}>
          <ModalHeader>
            <ModalClose onClick={this.hideAddModal}/>
            <ModalTitle>用户注册</ModalTitle>
          </ModalHeader>
          <ModalBody>
            <AddUser addCallback={this.addUserCallback.bind(this)}/>
          </ModalBody>
          <ModalFooter>
          </ModalFooter>
        </Modal>
      </div>)
  }
}

function mapStateToProps(state) {
  return {
    all: state.user.all
  };
}

export default connect(mapStateToProps, { fetchUserList, disableUser })(UserList);