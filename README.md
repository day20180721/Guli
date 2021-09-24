# Guli
###### 以下測試皆在`JMeter`與`VisualVM`中，且配置如下</br>
  ![image](https://github.com/day20180721/Guli/blob/dev/images/配置.png)
### 影響請求速度的原因
* 請求至目標伺服器所歷經的微服務多寡</br>
  ###### nginx -> spring gateway -> 伺服器 `TPS262`  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache.png)
  ###### spring gateway -> 伺服器 `TPS322` ![image](https://github.com/day20180721/Guli/blob/dev/images/gateway.png)
  ###### 伺服器 `TPS411` ![image](https://github.com/day20180721/Guli/blob/dev/images/direct.png)
* 目標伺服器處理該請求的複雜度

### 如何降低伺服器處理請求的速度
* Service返回的數據使用緩存
  * 使用Spring cache將`多查少改`的數據緩存</br>
    ###### 目標為減少與MySQL交互的次數，因為Redis是內存資料庫，查詢的速度遠大於MySQL
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
  `JMeter`中開啟 `TPS262` / 不開啟 `TPS183`
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache.png)
  ![image](https://github.com/day20180721/Guli/blob/dev/images/nocache.png)
  </br>
  </br>
  `VisualVM`中開啟 / 不開啟，兩圖為一組
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache-jvm-1.png)
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache-jvm-2.png)
  </br>
  ![image](https://github.com/day20180721/Guli/blob/dev/images/nocache-jvm-1.png)
  ![image](https://github.com/day20180721/Guli/blob/dev/images/nocache-jvm-2.png)
* Thymleaf開啟緩存
  開啟/不開啟
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache.png)
  ![image](https://github.com/day20180721/Guli/blob/dev/images/cache-thymleaf.png)

### 如何渲染頁面的速度
* 使用nginx動靜分離
