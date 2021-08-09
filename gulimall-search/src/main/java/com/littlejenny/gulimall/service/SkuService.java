package com.littlejenny.gulimall.service;

import com.littlejenny.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface SkuService {

    boolean saveBatch(List<SkuEsModel> models) throws IOException;
}
