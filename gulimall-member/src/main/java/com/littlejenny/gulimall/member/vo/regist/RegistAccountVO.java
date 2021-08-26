package com.littlejenny.gulimall.member.vo.regist;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class RegistAccountVO {
    private String username;
    private String password;
    private String phone;
}
