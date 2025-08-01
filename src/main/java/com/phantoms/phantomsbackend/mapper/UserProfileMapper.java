package com.phantoms.phantomsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phantoms.phantomsbackend.pojo.model.UserProfileModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfileModel> {
}