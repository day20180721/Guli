package com.littlejenny.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class DoneVO {
    private Long id;
    private List<DetailResultVO> items;
}
