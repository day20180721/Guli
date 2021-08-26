package com.littlejenny.common.exception.ware;

public class SubTractStockException extends RuntimeException{

    public SubTractStockException() {
        super("庫存減去異常");
    }
}
