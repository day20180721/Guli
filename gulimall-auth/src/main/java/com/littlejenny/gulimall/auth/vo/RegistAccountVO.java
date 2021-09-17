package com.littlejenny.gulimall.auth.vo;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
@ToString
@Data
public class RegistAccountVO {
    @NotEmpty(message = "用戶名稱輸入為空")
    @Length(min = 6,max = 18,message = "長度限制為6-18字元")
    private String username;
    @NotEmpty(message = "用戶密碼輸入為空")
    @Length(min = 6,max = 18,message = "長度限制為6-18字元")
    private String password;
    @NotEmpty(message = "用戶手機輸入為空")
    @Pattern(regexp = "(^09)\\d{8}$",message = "用戶手機格式錯誤")
    private String phone;
    @NotEmpty(message = "用戶驗證輸入為空")
    private String code;
}
