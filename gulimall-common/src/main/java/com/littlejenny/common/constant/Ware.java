package com.littlejenny.common.constant;

public class Ware {
    public enum PurchaseStatus {
        CREATED(0,"CREATED"),
        ASSIGNED(1,"RECEIVE"),
        RECEIVE(2,"RECEIVE"),
        FINISH(3,"FINISH"),
        HASERROR(4,"HASERROR");

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        PurchaseStatus(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        private int code;
        private String msg;
    }
    public enum PurchaseDetailStatus {
        CREATED(0,"CREATED"),
        ASSIGNED(1,"RECEIVE"),
        BUYING(2,"BUYING"),
        FINISH(3,"FINISH"),
        HASERROR(4,"HASERROR");

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        PurchaseDetailStatus(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        private int code;
        private String msg;
    }
}
