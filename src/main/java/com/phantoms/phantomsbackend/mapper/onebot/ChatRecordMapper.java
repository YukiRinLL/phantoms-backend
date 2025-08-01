package com.phantoms.phantomsbackend.mapper.onebot;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phantoms.phantomsbackend.pojo.model.onebot.ChatRecordModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatRecordMapper extends BaseMapper<ChatRecordModel> {
}