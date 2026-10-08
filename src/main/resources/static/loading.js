/**
 * 公共等待遮罩：慢接口请求期间显示全屏 loading（转圈 + 文案 + 已等待时长 + 耐心提示）。
 * 用法：
 *   showLoading('正在签到...', '可能需要等待 1 分钟，请不要重复点击');
 *   updateLoading('正在签到（2/5）...');
 *   try { ... } finally { hideLoading(); }
 * 支持引用计数，嵌套调用安全。
 */
(function () {
    const OVERLAY_ID = 'phantoms-loading-overlay';
    const STYLE_ID = 'phantoms-loading-style';

    let refCount = 0;
    let timer = null;
    let startAt = 0;

    function injectStyle() {
        if (document.getElementById(STYLE_ID)) return;
        const style = document.createElement('style');
        style.id = STYLE_ID;
        style.textContent = `
#${OVERLAY_ID} {
    position: fixed;
    inset: 0;
    z-index: 99999;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(43, 50, 59, 0.35);
    animation: phantoms-loading-fade 0.2s ease;
}
#${OVERLAY_ID} .pl-card {
    min-width: 260px;
    max-width: 380px;
    padding: 28px 32px;
    background: #FFFFFF;
    border-radius: 12px;
    box-shadow: 0 12px 40px rgba(43, 50, 59, 0.2);
    text-align: center;
    color: #2B323B;
}
#${OVERLAY_ID} .pl-spinner {
    width: 42px;
    height: 42px;
    margin: 0 auto 16px;
    border: 4px solid rgba(91, 111, 130, 0.18);
    border-top-color: #5B6F82;
    border-radius: 50%;
    animation: phantoms-loading-spin 0.8s linear infinite;
}
#${OVERLAY_ID} .pl-message {
    font-size: 15px;
    font-weight: 600;
    line-height: 1.5;
}
#${OVERLAY_ID} .pl-elapsed {
    margin-top: 6px;
    font-size: 13px;
    color: #909AA5;
    font-variant-numeric: tabular-nums;
}
#${OVERLAY_ID} .pl-hint {
    margin-top: 10px;
    font-size: 12.5px;
    color: #606A75;
    line-height: 1.6;
}
@keyframes phantoms-loading-spin { to { transform: rotate(360deg); } }
@keyframes phantoms-loading-fade { from { opacity: 0; } to { opacity: 1; } }
`;
        document.head.appendChild(style);
    }

    function ensureOverlay() {
        injectStyle();
        let overlay = document.getElementById(OVERLAY_ID);
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.id = OVERLAY_ID;
            overlay.innerHTML =
                '<div class="pl-card">' +
                '<div class="pl-spinner"></div>' +
                '<div class="pl-message"></div>' +
                '<div class="pl-elapsed"></div>' +
                '<div class="pl-hint"></div>' +
                '</div>';
            document.body.appendChild(overlay);
        }
        return overlay;
    }

    function renderElapsed() {
        const overlay = document.getElementById(OVERLAY_ID);
        if (!overlay) return;
        const seconds = Math.floor((Date.now() - startAt) / 1000);
        overlay.querySelector('.pl-elapsed').textContent = '已等待 ' + seconds + ' 秒';
    }

    window.showLoading = function (message, hint) {
        refCount++;
        const overlay = ensureOverlay();
        overlay.querySelector('.pl-message').textContent = message || '处理中...';
        overlay.querySelector('.pl-hint').textContent = hint || '接口响应可能较慢，请耐心等待，不要刷新或重复点击';
        overlay.querySelector('.pl-elapsed').textContent = '已等待 0 秒';
        if (timer) clearInterval(timer);
        startAt = Date.now();
        timer = setInterval(renderElapsed, 1000);
    };

    window.updateLoading = function (message, hint) {
        const overlay = document.getElementById(OVERLAY_ID);
        if (!overlay) return;
        if (message) overlay.querySelector('.pl-message').textContent = message;
        if (hint !== undefined) overlay.querySelector('.pl-hint').textContent = hint || '';
    };

    window.hideLoading = function () {
        refCount = Math.max(0, refCount - 1);
        if (refCount > 0) return;
        if (timer) {
            clearInterval(timer);
            timer = null;
        }
        const overlay = document.getElementById(OVERLAY_ID);
        if (overlay) overlay.remove();
    };

    /**
     * 把 fetch 抛出的网络错误转换为对用户友好的中文提示。
     * 免费版服务空闲休眠后首次响应可能需要 1 分钟左右。
     */
    window.friendlyNetworkError = function (error) {
        if (!error) return '未知错误';
        if (error.name === 'AbortError') return '请求已取消';
        const msg = String(error.message || '');
        if (/Failed to fetch|NetworkError|Network request failed|load failed/i.test(msg)) {
            return '网络异常或服务正在唤醒中（空闲服务首次响应可能需要 1 分钟左右），请稍后再试';
        }
        return msg;
    };
})();
