# Guli
###### 以下測試皆在`JMeter`與`VisualVM`中，且配置如下</br>
  ![image](https://github.com/day20180721/Guli/blob/dev/images/配置.png)
### 影響請求速度的原因
* 請求至目標伺服器所歷經的微服務多寡</br>
  ###### nginx -> spring gateway -> 伺服器 ![image](https://github.com/day20180721/Guli/blob/dev/images/cache.png)
  ###### spring gateway -> 伺服器 ![image](https://github.com/day20180721/Guli/blob/dev/images/gateway.png)
  ###### 伺服器 ![image](https://github.com/day20180721/Guli/blob/dev/images/direct.png)
* 目標伺服器處理該請求的複雜度

### 如何降低伺服器處理請求的速度
* Service返回的數據使用緩存
  * 使用Spring cache將`多查少改`的數據緩存
  ```java
    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public List<CategoryEntity> getAllLevelOne() {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("cat_level",1);
        List<CategoryEntity> levelOneList = baseMapper.selectList(wrapper);
        return levelOneList;
    }
  ```
  `JMeter`中開啟/不開啟
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache.png)
  ![image](https://github.com/day20180721/Guli/blob/dev/images/nocache.png)
  沒有緩存除了程式運行時間增加外，如果該變量內存占用超過JVM新生代就會導致此變量直接進入老年區，而反覆的請求就會讓老年區快速被占滿，最後強制FullGC
  `VisualVM`中開啟/不開啟
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache-jvm-1.png)
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache-jvm-2.png)
  ---
  ![image](https://github.com/day20180721/Guli/blob/dev/images/nocache-jvm-1.png)
  ![image](https://github.com/day20180721/Guli/blob/dev/images/nocache-jvm-2.png)
* Thymleaf開啟緩存
  開啟/不開啟
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache.png)
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache-thymleaf.png)
  
