package com.littlejenny.gulimall.member.dao;

import com.littlejenny.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员
 * 
 * @author littlejenny
 * @email day20180721@gmail.com
 * @date 2021-07-16 17:32:06
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    Integer accountIsExist(@Param("username") String username);

}
