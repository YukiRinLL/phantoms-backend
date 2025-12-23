# 石之家数据获取流程说明

## 扫码登录

**登录地址：** https://phantoms-backend.onrender.com/login.html

![RisingStones Login.png](RisingStones%20Login.png)

直接在库表中修改值，Scheduler会每30分钟一次定时获取并更新缓存。
也可以使用以下API更新/检查配置

## 石之家登录流程

### 登录步骤：

1. **获取二维码** https://w.cas.sdo.com/authen/getcodekey.jsonp?
2. **扫码登录**
3. **登陆成功缓存cookie信息到DB**

## 数据更新说明
**石之家数据更新时间：** 中午12点更新截止至前一天22:00的数据

## 注意事项
- 登录过程中需要保持网络连接稳定
- Cookie有效期后需要重新获取