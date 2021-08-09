package com.littlejenny.gulimall.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/thirdPart/azure")
public class BlobController {
    @Value("${azure.storage.baseblobUrl}")
    public String baseUrl;
    @Value("${azure.storage.signature}")
    public String signature;
    @GetMapping("/bloburl")
    public Map blobUrl(){
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        String date = ft.format(new Date());
        Map map = new HashMap();
        map.put("url",baseUrl);
        map.put("date",date);
        map.put("container","brandicon");
        map.put("signature",signature);
        return map;
    }
    @PostMapping("/hello")
    public String hello(){
        return "hello";
    }
}