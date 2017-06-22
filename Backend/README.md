## 基于Fabric的电子发票

## 测

### 环境准备

## 运行

## 电子发票 API

API 基于前端所需数据, 参考样例数据见[这里](https://github.com/zkjs/oxchain-invoice/blob/master/db.json).

RESTful API 包括以下资源的处理:
    - 普通用户(`/user`)
    - 企业用户(`/org`)
    - 发票(`/invoice`)
    - 报销(`/reimbursement`)
    - 登录令牌(`/token`)

### 数据模型

 实体 | 字段 
-----|-----
 普通用户 | id, username, password, mobile
 企业用户 | id, traderId, name, password, mobile
 发票 | id, company, date, description, history, origin, owner, type, state
 报销 | id, invoices, state, initiator, date
 消费记录 | id, date, items, total, state

