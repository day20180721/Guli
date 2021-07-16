package com.littlejenny.gulimall.member.service;

import com.littlejenny.gulimall.member.entity.MemberLevelEntity;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class MemberLevelServiceTest {
    @Autowired
    MemberLevelService service;
    @Test
    public void save() {
        MemberLevelEntity memberLevelEntity = new MemberLevelEntity();
        memberLevelEntity.setName("Testing");
        service.save(memberLevelEntity);
    }
}