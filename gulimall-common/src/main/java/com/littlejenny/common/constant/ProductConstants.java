package com.littlejenny.common.constant;

public class ProductConstants {

    public enum AttrSearchAble{
        CANNOT(0,"UnSearchable"),
        CAN(1,"Searchable");
        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        AttrSearchAble(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        private int code;
        private String msg;
    }
    public enum SpuPublishStatus {
        NEW(0,"NEW"),
        ON(1,"ON"),
        OFF(2,"OFF");

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        SpuPublishStatus(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        private int code;
        private String msg;
    }
    public enum AttrTypeEnum {
        ATTR_TYPE_BASE(1,"Basic property"),
        ATTR_TYPE_SALE(0,"sale property");

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        AttrTypeEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }
        private int code;
        private String msg;


    }
}
