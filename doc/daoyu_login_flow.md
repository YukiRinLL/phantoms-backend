# 叨鱼（Daoyu）登录流程

## 概述
叨鱼登录是FF14签到助手（ffxiv-signin-helper）的核心功能之一，用于获取登录状态以便进行自动签到等操作。

## 完整登录流程

### 1. 准备工作
- 设置用户代理（User-Agent）
- 创建CookieJar用于管理会话cookies
- 设置初始cookies（__wftflow和userinfo）

### 2. 获取登录二维码
```typescript
const getLoginQRCode = async (appID: string, areaID: string): Promise<string> => {
    // 发送请求获取二维码图片
    const req = await fetchCookie(`https://w.cas.sdo.com/authen/getcodekey.jsonp?${params}`, { headers });
    const res = await req.arrayBuffer();
    
    // 解析二维码
    const image = await Jimp.read(res);
    const content = jsQR(new Uint8ClampedArray(image.bitmap.data), image.bitmap.width, image.bitmap.height);
    
    return content.data;
};
```
- 请求URL：`https://w.cas.sdo.com/authen/getcodekey.jsonp`
- 参数：appId, areaId, maxsize, r（随机数）
- 返回：二维码图片，解析后得到登录链接

### 3. 显示二维码并等待扫描
```typescript
const loginQRCode = await getLoginQRCode('6788', '1');
console.info(await QRCode.toString(loginQRCode, { type: 'terminal', small: true }));
console.info('请使用叨鱼扫描二维码登录');
```
- 使用qrcode库在终端显示二维码
- 提示用户使用叨鱼App扫描二维码

### 4. 轮询登录状态
```typescript
const waitLoginQRCode = async (appID: string, areaID: string, serviceURL: string): Promise<string> => {
    const startTime = Date.now();
    
    while (Date.now() - startTime < 60 * 1000) {
        const res = await getLoginInfo(appID, areaID, serviceURL);
        
        if ('ticket' in res) {
            return res.ticket;
        }
        
        if (res.mappedErrorCode === -10515801) {
            throw new Error('二维码已失效');
        }
        
        await new Promise((resolve) => { setTimeout(resolve, 1000); });
    }
    
    throw new Error('二维码扫描超时');
};
```
- 每隔1秒轮询登录状态
- 超时时间：60秒
- 成功时返回ticket
- 失败时抛出相应错误

### 5. 完成登录
```typescript
const finishLogin = async (ticket: string, redirectURL: string): Promise<void> => {
    const params = new URLSearchParams();
    params.set('ticket', ticket);
    params.set('redirectUrl', redirectURL);
    
    await fetchCookie(`http://apiff14risingstones.web.sdo.com/api/home/GHome/login?${params}`, { headers });
};
```
- 使用获取到的ticket完成登录
- 请求URL：`http://apiff14risingstones.web.sdo.com/api/home/GHome/login`
- 参数：ticket, redirectUrl

### 6. 保存登录状态
```typescript
const cookies = cookieJar.getCookieStringSync('https://apiff14risingstones.web.sdo.com/');
fs.writeFileSync('.cookies', cookies);
```
- 从CookieJar中获取完整的cookies字符串
- 将cookies保存到本地.cookies文件中

## 登录后使用

登录获取cookies后，可以用于后续的签到等操作：

### 1. 获取cookies的方式
```typescript
export default async (host: string, domain: string): Promise<string> => {
    // 1. 从环境变量获取
    if (COOKIES.length > 0) {
        return COOKIES;
    }
    
    // 2. 从CookieCloud获取
    if (COOKIE_CLOUD_URL && COOKIE_CLOUD_UUID && COOKIE_CLOUD_KEY) {
        // ... 从CookieCloud获取并解密cookies
        return cookies;
    }
    
    // 3. 从本地文件获取
    const cookiesFile = path.resolve(fileURLToPath(import.meta.url), '..', '..', '.cookies');
    const cookies = await fs.readFile(cookiesFile, { encoding: 'utf-8' });
    
    return cookies;
};
```

### 2. 使用cookies进行签到
```typescript
export default async (cookies: string) => {
    await getLoginInfo(cookies);
    
    const characterInfo = await getCharacterBindInfo(cookies);
    if (characterInfo.isSign !== 1) {
        const signInResult = await doSignIn(cookies);
        // ... 处理签到结果
    }
    
    // ... 其他操作
};
```

## 技术要点

### 使用的库
- `fetch-cookie` 和 `tough-cookie`：处理cookies
- `Jimp` 和 `jsqr`：解析二维码
- `qrcode`：在终端显示二维码
- `node-crypto`：加密解密操作（用于CookieCloud）

### 关键参数
- appId: `6788`（FF14的应用ID）
- areaId: `1`
- serviceURL: `http://apiff14risingstones.web.sdo.com/api/home/GHome/login?redirectUrl=https://ff14risingstones.web.sdo.com/pc/index.html`
- redirectURL: `https://ff14risingstones.web.sdo.com/pc/index.html`

### 注意事项
- 登录过程需要用户手动扫描二维码
- 二维码有一定的有效期（约60秒）
- 登录成功后，cookies会保存到本地文件供后续使用
- 可以通过环境变量、CookieCloud或本地文件三种方式获取cookies

## 代码位置
- 主要登录流程：`src/scripts/cookies.ts`
- cookies管理：`src/cookies.ts`
- 应用入口：`src/app.ts`
- 主要功能：`src/main.ts`