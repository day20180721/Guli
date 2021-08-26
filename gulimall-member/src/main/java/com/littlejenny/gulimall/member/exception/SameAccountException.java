package com.littlejenny.gulimall.member.exception;

public class SameAccountException extends RuntimeException{
    public SameAccountException() {
        super("此帳號已經註冊");
    }
}
