package com.littlejenny.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class AttrResponseVO extends AttrVO{
    private String catelogName;
    private String groupName;
    private Long[] catelogPath;
}
