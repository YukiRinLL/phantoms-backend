package com.phantoms.phantomsbackend.common.utils.TH;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

@Component
public class MyIdGenerator implements IdentifierGenerator {
    private static final Snowflake snowflake = IdUtil.createSnowflake(1, 1);

    @Override
    public Long nextId(Object entity) {
        long id = snowflake.nextId();
        // 将生成的 ID 转换为 16 位数字，然后再转换为 Long 类型
        String idStr = String.valueOf(id);
        if (idStr.length() < 16) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16 - idStr.length(); i++) {
                sb.append('0');
            }
            idStr = sb.toString() + idStr;
        } else if (idStr.length() > 16) {
            idStr = idStr.substring(idStr.length() - 16);
        }
        return Long.parseLong(idStr);
    }
}