## 基于Fabric的电子发票

### 环境准备

安装最新的 [chaincode](./chaincode)

部署时注意覆盖默认数据库和 [fabric-manage](https://github.com/zkjs/fabric-manage) 相关配置.

## 电子发票 API

RESTful API 包括以下资源的处理:

- 普通用户(`/user`)
- 企业用户(`/org`)
- 发票(`/invoice`)
- 报销(`/reimbursement`)
- 登录令牌(`/token`)

