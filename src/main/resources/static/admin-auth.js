/**
 * 管理页面公共鉴权：请求自动附带 X-Admin-Key 密码，403 时弹窗输入并重试。
 * 密码保存在 localStorage，与 Vue 管理端共用同一存储键。
 */
const PHANTOMS_ADMIN_KEY_STORAGE = 'phantoms_admin_key';

function getAdminKey() {
    return localStorage.getItem(PHANTOMS_ADMIN_KEY_STORAGE) || '';
}

async function promptAdminKey() {
    const key = prompt('请输入管理员密码：');
    if (key !== null && key.trim()) {
        localStorage.setItem(PHANTOMS_ADMIN_KEY_STORAGE, key.trim());
        return key.trim();
    }
    return null;
}

async function adminFetch(url, options = {}) {
    const doFetch = (key) => {
        const headers = new Headers(options.headers || {});
        if (key) headers.set('X-Admin-Key', key);
        return fetch(url, Object.assign({}, options, { headers }));
    };

    let response = await doFetch(getAdminKey());
    if (response.status === 403) {
        const newKey = await promptAdminKey();
        if (newKey) {
            response = await doFetch(newKey);
        }
    }
    return response;
}
