package com.phantoms.phantomsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phantoms.phantomsbackend.pojo.model.MessageModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<MessageModel> {
}