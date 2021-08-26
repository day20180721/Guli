package com.littlejenny.gulimall.to;

import com.littlejenny.common.validanno.ListVal;
import com.littlejenny.common.validgroup.AddGroup;
import com.littlejenny.common.validgroup.UpdateGroup;
import com.littlejenny.common.validgroup.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

@Data
public class BrandTO {
    private Long brandId;
    private String name;
    private String logo;
    private String descript;
    private Integer showStatus;
    private String firstLetter;
    private Integer sort;
}
