# Rising Stones (FF14 石之家) API 接口文档

> 本文档整理了与 SDO 源服务器交互的所有 API 接口，包括请求方式、URL、参数、Headers 及示例响应。

---

## 基础信息

- **基础域名**: `https://apiff14risingstones.web.sdo.com`
- **登录域名**: `https://w.cas.sdo.com`
- **User-Agent**: `Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36`
- **Referer**: `https://ff14risingstones.web.sdo.com/`
- **登录 Referer**: `https://login.u.sdo.com/`
- **APP_ID**: `6788`
- **AREA_ID**: `1`

---

## 一、登录流程 API

### 1.1 获取登录二维码

**请求**:
- **Method**: `GET`
- **URL**: `https://w.cas.sdo.com/authen/getcodekey.jsonp`
- **Query Parameters**:
  - `appId`: `6788`
  - `areaId`: `1`
  - `maxsize`: `145`
  - `r`: 随机数 (如 `0.123456789`)
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Referer`: https://login.u.sdo.com/

**成功响应**: 返回一张二维码图片 (PNG 格式)

**处理逻辑**: 后端读取图片，使用 ZXing 库解析二维码内容，得到登录 URL (作为 ticket 用途)

---

### 1.2 检查登录状态 (轮询)

**请求**:
- **Method**: `GET`
- **URL**: `https://w.cas.sdo.com/authen/codeKeyLogin.jsonp`
- **Query Parameters**:
  - `appId`: `6788`
  - `areaId`: `1`
  - `serviceUrl`: `http://apiff14risingstones.web.sdo.com/api/home/GHome/login?redirectUrl=https://ff14risingstones.web.sdo.com/pc/index.html`
  - `callback`: `codeKeyLogin_JSONPMethod`
  - `code`: `300`
  - `productId`: `2`
  - `productVersion`: `3.1.0`
  - `authenSource`: `2`
  - `_`: 当前时间戳 (毫秒)
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Referer`: https://login.u.sdo.com/

**响应示例** (等待扫描):
```json
codeKeyLogin_JSONPMethod({
  "code": 10000,
  "data": {
    "mappedErrorCode": 0
  }
})
```

**响应示例** (扫描成功):
```json
codeKeyLogin_JSONPMethod({
  "code": 10000,
  "data": {
    "ticket": "xxxxxxxxx-ticket-value-xxxxxxxx"
  }
})
```

**响应示例** (二维码过期):
```json
codeKeyLogin_JSONPMethod({
  "code": 10000,
  "data": {
    "mappedErrorCode": -10515801
  }
})
```

---

### 1.3 完成登录 (交换 Cookie)

**请求**:
- **Method**: `GET`
- **URL**: `http://apiff14risingstones.web.sdo.com/api/home/GHome/login`
- **Query Parameters**:
  - `ticket`: 从步骤 1.2 获取的 ticket
  - `redirectUrl`: `https://ff14risingstones.web.sdo.com/pc/index.html`
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Referer`: https://login.u.sdo.com/

**成功响应**: HTTP 302 重定向，响应头中包含 Set-Cookie:

**获得的 Cookies**:

| 域名 | Cookie 名 | 说明 |
|------|-----------|------|
| w.cas.sdo.com | CODEKEY_COUNT | 验证码计数 |
| w.cas.sdo.com | CASTGC | CAS 全局 Cookie |
| w.cas.sdo.com | SECURE_CASTGC | 安全 CAS Cookie |
| w.cas.sdo.com | CASCID | CAS 会话 ID |
| w.cas.sdo.com | SECURE_CODEKEY | 安全验证码 Key |
| w.cas.sdo.com | SECURE_CASCID | 安全 CAS 会话 ID |
| w.cas.sdo.com | CODEKEY | 验证码 Key |
| w.cas.sdo.com | CAS_LOGIN_STATE | CAS 登录状态 |
| w.cas.sdo.com | SECURE_CAS_LOGIN_STATE | 安全 CAS 登录状态 |
| w.cas.sdo.com | SECURE_CODEKEY_COUNT | 安全验证码计数 |
| w.cas.sdo.com | sdo_dw_track | SDO 追踪 Cookie |
| apiff14risingstones.web.sdo.com | NSC_JOyjyufgb1jqla5dwyzke3dddubgec0 | 网域安全 Cookie |
| apiff14risingstones.web.sdo.com | __wftflow | 流程标识 Cookie |
| apiff14risingstones.web.sdo.com | ff14risingstones | 登录会话 Cookie |
| apiff14risingstones.web.sdo.com | userinfo | 用户信息 (包含 userid 和 siteid) |

**userinfo Cookie 格式**: `userid={UUID}-{时间戳}-{递增ID}&siteid=SDG-08132-01`

---

## 二、账号状态检查 API

### 2.1 检查登录状态

**请求**:
- **Method**: `GET`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/GHome/isLogin`
- **Query Parameters**:
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/

**响应示例**:
```json
{
  "code": 10000,
  "data": {
    "isLogin": true
  }
}
```

---

### 2.2 获取角色绑定信息

**请求**:
- **Method**: `GET`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/groupAndRole/getCharacterBindInfo`
- **Query Parameters**:
  - `platform`: `1`
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/

**响应示例** (未绑定角色):
```json
{
  "code": 10103,
  "msg": "请先绑定角色",
  "data": []
}
```

**响应示例** (成功):
```json
{
  "code": 10000,
  "msg": "success",
  "data": [
    {
      "characterName": "角色名",
      "serverName": "服务器名",
      "groupId": "部队ID",
      "groupName": "部队名",
      "avatar": "头像URL",
      "experience": "经验值",
      "uuid": "用户UUID"
    }
  ]
}
```

**注意**: 在账号列表页面，此接口会被多次调用（每个账号调用一次 + 默认账号额外调用）

---

## 三、用户信息 API

### 3.1 获取用户基础信息

**请求**:
- **Method**: `GET`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/userInfo/getUserInfo`
- **Query Parameters**:
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/

**响应示例**:
```json
{
  "code": 10000,
  "data": {
    "userId": "用户ID",
    "nickname": "昵称",
    "avatar": "头像URL",
    "characterName": "角色名",
    "serverName": "服务器名"
  }
}
```

---

### 3.2 获取指定用户信息

**请求**:
- **Method**: `GET`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/userInfo/getUserInfo`
- **Query Parameters**:
  - `uuid`: 目标用户 UUID
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/

---

## 四、签到相关 API

### 4.1 执行签到

**请求**:
- **Method**: `POST`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/sign/signIn`
- **Query Parameters**:
  - `tempsuid`: 随机 UUID
- **Request Body** (form-urlencoded):
  - `tempsuid`: 随机 UUID (与 URL 参数相同)
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/
  - `Content-Type`: application/x-www-form-urlencoded

**成功响应** (code=10001):
```json
{
  "code": 10001,
  "msg": "签到成功",
  "data": {
    "signInTime": "2026-07-29 00:00:00",
    "reward": "奖励内容"
  }
}
```

**失败响应** (code=10103 - 未绑定角色):
```json
{
  "code": 10103,
  "msg": "请先绑定角色",
  "data": []
}
```

**成功响应** (code=10001 - 已签到):
```json
{
  "code": 10001,
  "msg": "今天已签到过啦，不要再签了^.^"
}
```

**成功响应** (code=10000 - 签到成功，包含签到详情):
```json
{
  "msg": "操作成功",
  "code": 10000,
  "data": {
    "shopExp": 100,
    "totalDays": "6",
    "sqExp": 3,
    "sqMsg": "石之家社区签到成功，商城签到成功",
    "continuousDays": 1
  }
}
```

**字段说明**:
- `shopExp`: 商城经验值
- `totalDays`: 本月累计签到天数
- `sqExp`: 社区经验值
- `sqMsg`: 签到消息
- `continuousDays`: 连续签到天数

**失败响应** (频率限制 code=10301):
```json
{
  "code": 10301,
  "msg": "操作太快，请稍后再试"
}
```

**失败响应** (已签到):
```json
{
  "code": 10102,
  "msg": "今日已签到",
  "data": []
}
```

---

### 4.2 获取签到日志

**请求**:
- **Method**: `GET`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/sign/mySignLog`
- **Query Parameters**:
  - `month`: 月份 (如 `2026-07`)
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/

**响应示例**:
```json
{
  "code": 10000,
  "data": [
    {
      "date": "2026-07-01",
      "status": 1,
      "reward": "奖励内容"
    }
  ]
}
```

---

## 五、签到奖励 API

### 5.1 获取签到奖励列表

**请求**:
- **Method**: `GET`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/sign/signRewardList`
- **Query Parameters**:
  - `month`: 月份 (如 `2026-07`)
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/

**成功响应** (完整数据):
```json
{
  "code": 10000,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "item_name": "传送网使用券*30",
      "item_desc": "本月签到10天奖励",
      "item_pic": "https://fu5.web.sdo.com/10036/202312/17025421018998.png",
      "num": 1,
      "rule": 10,
      "is_get": 0,
      "begin_date": "2023-11-01 00:00:00",
      "end_date": "2030-12-31 23:59:59"
    },
    {
      "id": 2,
      "item_name": "奖励名称2",
      "item_desc": "本月签到20天奖励",
      "item_pic": "https://fu5.web.sdo.com/xxx/yyy.png",
      "num": 1,
      "rule": 20,
      "is_get": -1,
      "begin_date": "2023-11-01 00:00:00",
      "end_date": "2030-12-31 23:59:59"
    },
    {
      "id": 3,
      "item_name": "奖励名称3",
      "item_desc": "本月签到30天奖励",
      "item_pic": "https://fu5.web.sdo.com/xxx/yyy.png",
      "num": 1,
      "rule": 30,
      "is_get": -1,
      "begin_date": "2023-11-01 00:00:00",
      "end_date": "2030-12-31 23:59:59"
    }
  ]
}
```

**字段说明**:
- `is_get`: `0` = 可领取, `-1` = 未达成
- `rule`: 需要连续签到天数
- `item_name`: 奖励物品名称
- `item_pic`: 奖励物品图标 URL

**失败响应** (未绑定角色):
```json
{
  "code": 10103,
  "msg": "请先绑定角色",
  "data": []
}
```

---

### 5.2 领取签到奖励

**请求**:
- **Method**: `POST`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/sign/getSignReward`
- **Query Parameters**:
  - `tempsuid`: 随机 UUID
- **Request Body** (form-urlencoded):
  - `id`: 奖励 ID (整数)
  - `month`: 月份 (如 `2026-07`)
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/
  - `Content-Type`: application/x-www-form-urlencoded

**成功响应**:
```json
{
  "code": 10000,
  "msg": "success",
  "data": {
    "rewardId": 1,
    "rewardName": "奖励名称",
    "receivedAt": "2026-07-29T00:00:00"
  }
}
```

---

## 六、部队相关 API

### 6.1 获取部队信息

**请求**:
- **Method**: `GET`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/guild/getGuildInfo`
- **Query Parameters**:
  - `guild_id`: 部队 ID
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/

---

### 6.2 获取部队成员列表

**请求**:
- **Method**: `GET`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/guild/getGuildMember`
- **Query Parameters**:
  - `guild_id`: 部队 ID
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/

---

### 6.3 获取部队成员动态

**请求**:
- **Method**: `GET`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/guild/guildMemberDynamic`
- **Query Parameters**:
  - `guild_id`: 部队 ID
  - `page`: 页码
  - `limit`: 每页数量
  - `tempsuid`: 随机 UUID
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/

---

## 七、动态相关 API

### 7.1 创建动态

**请求**:
- **Method**: `POST`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/post/createPost`
- **Request Body** (JSON):
  ```json
  {
    "content": "动态内容",
    "scope": 1,
    "pic_url": "图片URL"
  }
  ```
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/
  - `Content-Type`: application/json

---

### 7.2 创建动态评论

**请求**:
- **Method**: `POST`
- **URL**: `https://apiff14risingstones.web.sdo.com/api/home/post/createPostComment`
- **Request Body** (form-urlencoded):
  - `content`: 评论内容
  - `posts_id`: 动态 ID
  - `parent_id`: 父评论 ID (默认 `0`)
  - `root_parent`: 根评论 ID (默认 `0`)
  - `comment_pic`: 评论图片 URL
- **Headers**:
  - `User-Agent`: Mozilla/5.0 ...
  - `Cookie`: 完整的 Cookie 字符串
  - `Referer`: https://ff14risingstones.web.sdo.com/
  - `Content-Type`: application/x-www-form-urlencoded

---

## 八、响应码说明

| Code | 含义 | 说明 |
|------|------|------|
| 10000 | 成功 | 请求成功处理 (签到、奖励等操作的通用成功码) |
| 10001 | 签到成功/已签到 | 签到操作成功 (或今日已签到) |
| 10102 | 已签到 | 今日已完成签到 |
| 10103 | 请先绑定角色 | 账号未绑定游戏角色 |
| 10301 | 操作太快 | 请求频率过高，请稍后再试 |
| -10515801 | 二维码过期 | 登录二维码已失效 |

### 签到响应说明
- `code: 10000` + `msg: "操作成功"` → 签到成功，返回签到详情（如连续签到天数、经验值等）
- `code: 10001` + `msg: "签到成功"` 或 `"今天已签到过啦"` → 签到成功或已签到
- 其他 code → 签到失败

---

## 九、Cookie 管理说明

### 初始化 Cookie
```java
// 构造函数中初始化
cookieJar.saveFromResponse("https://apiff14risingstones.web.sdo.com/", 
    Cookie.parse("https://apiff14risingstones.web.sdo.com/", 
        "__wftflow=1607418051=1"));

cookieJar.saveFromResponse("https://apiff14risingstones.web.sdo.com/", 
    Cookie.parse("https://apiff14risingstones.web.sdo.com/", 
        "userinfo=userid=" + userID + "&siteid=SDG-08132-01"));
```

### 请求 Cookie 格式
```
Cookie: name1=value1; name2=value2; ...
```

### Cookie 组成
1. `__wftflow`: 流程标识，初始值 `1607418051=1`
2. `userinfo`: 用户信息，格式 `userid={ID}&siteid=SDG-08132-01`
3. 登录后会获得更多 Cookie（见 1.3 节）

---

## 十、完整请求流程示例

### 新账号登录流程
```
1. GET  /authen/getcodekey.jsonp           → 获取二维码图片
2. GET  /authen/codeKeyLogin.jsonp          → 轮询检查扫码状态 (多次)
3. GET  /api/home/GHome/login              → 使用 ticket 完成登录，获取 Cookie
4. GET  /api/home/groupAndRole/getCharacterBindInfo → 获取角色绑定信息
```

### 签到流程
```
1. POST /api/home/sign/signIn              → 执行签到
2. GET  /api/home/sign/signRewardList      → 获取奖励列表
3. POST /api/home/sign/getSignReward       → 领取奖励 (遍历 is_get=0 的奖励)
```

---

## 十一、已记录的请求示例

### 账号操作汇总 (2026-07-29 16:58)

| 账号 | 签到结果 | 奖励列表 | 备注 |
|------|----------|----------|------|
| 709418c23cd74c95 | 已签到 (10001) | 成功 (10000) | cookie 长度 185, 无 userinfo |
| cf2f4807a22d4549 | 已签到 (10001) | 成功 (10000) | cookie 长度 187, 无 userinfo |
| e2e042784cfe4e41 | 已签到 (10001) | 成功 (10000) | cookie 长度 272, 含 userinfo |
| f006d4d4ecd44813 | 未绑定角色 (10103) | 未绑定角色 (10103) | 新账号, cookie 长度 692 |

---

### 新账号 f006d4d4ecd44813 登录记录

**时间**: 2026-07-29 16:53:02

**登录完成** (步骤 3):
```
GET http://apiff14risingstones.web.sdo.com/api/home/GHome/login?ticket=xxx&redirectUrl=https://ff14risingstones.web.sdo.com/pc/index.html

Response: 302 Found
Cookies received:
  w.cas.sdo.com: [CODEKEY_COUNT, CASTGC, SECURE_CASTGC, CASCID, SECURE_CODEKEY, SECURE_CASCID, CODEKEY, CAS_LOGIN_STATE, SECURE_CAS_LOGIN_STATE, SECURE_CODEKEY_COUNT, sdo_dw_track]
  apiff14risingstones.web.sdo.com: [NSC_JOyjyufgb1jqla5dwyzke3dddubgec0, __wftflow, ff14risingstones, userinfo]
```

**角色绑定检查**:
```
GET https://apiff14risingstones.web.sdo.com/api/home/groupAndRole/getCharacterBindInfo?platform=1&tempsuid={UUID}

Response: 200 OK
Body: {"code":10103,"msg":"请先绑定角色","data":[]}
```

**签到请求** (失败 - 未绑定角色):
```
POST https://apiff14risingstones.web.sdo.com/api/home/sign/signIn?tempsuid={UUID}
Body: tempsuid={UUID}

Response: 200 OK
Body: {"code":10103,"msg":"请先绑定角色","data":[]}
```

**奖励列表请求** (失败):
```
GET https://apiff14risingstones.web.sdo.com/api/home/sign/signRewardList?month=2026-07&tempsuid={UUID}

Response: 200 OK
Body: {"msg":"请先绑定角色","code":10103,"data":[]}
```

---

### 账号 709418c23cd74c95 签到记录

**时间**: 2026-07-29 16:58:14

**签到请求**:
```
POST https://apiff14risingstones.web.sdo.com/api/home/sign/signIn?tempsuid=defef5e6-5247-4805-9201-961e9b9adc14
Headers:
  Cookie: [长度185, 含 ff14risingstones=true, 无 userinfo]
  User-Agent: Mozilla/5.0 ...

Response: 200 OK
Body: {"code":10001,"msg":"今天已签到过啦，不要再签了^.^"}
```

**奖励列表请求**:
```
GET https://apiff14risingstones.web.sdo.com/api/home/sign/signRewardList?month=2026-07&tempsuid={UUID}

Response: 200 OK
Body (完整):
{
  "msg": "操作成功",
  "code": 10000,
  "data": [
    {
      "id": 1,
      "item_name": "传送网使用券*30",
      "item_desc": "本月签到10天奖励",
      "item_pic": "https://fu5.web.sdo.com/10036/202312/17025421018998.png",
      "num": 1,
      "rule": 10,
      "is_get": -1,
      "begin_date": "2023-11-01 00:00:00",
      "end_date": "2030-12-31 23:59:59"
    },
    {
      "id": 2,
      "item_name": "其他奖励",
      "rule": 20,
      "is_get": -1,
      ...
    },
    {
      "id": 3,
      "item_name": "其他奖励",
      "rule": 30,
      "is_get": -1,
      ...
    }
  ]
}
```

---

### 账号 cf2f4807a22d4549 签到记录

**时间**: 2026-07-29 16:58:19

**签到请求**:
```
POST https://apiff14risingstones.web.sdo.com/api/home/sign/signIn?tempsuid=5c53076d-4ce6-4c24-97b8-3b01b7fad88d
Headers:
  Cookie: [长度187, 含 ff14risingstones=true, 无 userinfo]

Response: 200 OK
Body: {"code":10001,"msg":"今天已签到过啦，不要再签了^.^"}
```

**奖励列表请求**:
```
Response: 200 OK
Body: {"msg":"操作成功","code":10000,"data":[...]}  // is_get=-1, 未达成
```

---

### 账号 e2e042784cfe4e41 签到记录

**时间**: 2026-07-29 16:58:32

**签到请求**:
```
POST https://apiff14risingstones.web.sdo.com/api/home/sign/signIn?tempsuid=a4283fb9-58b4-4f8e-be26-eabf16028187
Headers:
  Cookie: [长度272, 含 ff14risingstones=true, 含 userinfo=true]
  userid: 445385824-1424914049-1785143808

Response: 200 OK
Body: {"code":10001,"msg":"今天已签到过啦，不要再签了^.^"}
```

**奖励列表请求** (注意: 此账号奖励可领取):
```
Response: 200 OK
Body: {"msg":"操作成功","code":10000,"data":[
  {"id":1,"item_name":"传送网使用券*30","rule":10,"is_get":0,...},  // is_get=0 可领取!
  {"id":2,...,"is_get":-1},
  {"id":3,...,"is_get":-1}
]}
```

---

### 频率限制示例

**时间**: 2026-07-29 16:58:40

当快速连续签到时触发频率限制:

```
POST https://apiff14risingstones.web.sdo.com/api/home/sign/signIn?tempsuid=5fc4a182-a6e4-4124-869e-844b8ac0307d

Response: 200 OK
Body: {"code":10301,"msg":"操作太快，请稍后再试"}
```

---

### Cookie 格式对比

**无 userinfo 的 Cookie** (账号 709418c23cd74c95, cf2f4807a22d4549):
```
__wftflow=xxx; ff14risingstones=xxx; NSC_JOyjyufgb1jqla5dwyzke3dddubgec0=xxx
(长度约 185-187 字符, 无完整 SDO 登录态)
```

**含 userinfo 的 Cookie** (账号 e2e042784cfe4e41, f006d4d4ecd44813):
```
CODEKEY_COUNT=xxx; CASCID=xxx; SECURE_CASCID=xxx; ...; userinfo=userid=445385824-{timestamp}-{id}&siteid=SDG-08132-01; ff14risingstones=xxx; NSC_JOyjyufgb1jqla5dwyzke3dddubgec0=xxx; ...
(长度约 272-692 字符, 含完整 SDO 登录态)
```

**注意**: 含 `userinfo` 的 Cookie 是从完整登录流程获得的，包含 SSO 登录态；不含 `userinfo` 的 Cookie 可能是旧 Cookie 或不完整登录
