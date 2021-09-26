# Guli
### 自動生成基礎代碼
提供資料表給renren-generator，會生成對應該表的CRUD(Controller、Service、Dao)及前端Vue介面(基於renren-fast)
### 秒殺
面對高併發的流量，我們需要在最短時間內處理完請求，所以處理一個請求的過程為</br>
`判斷是否已經購買 -> 請求Redis信號量 --成功-> 發送購買成功的Rabbitmq --> 達到最終一致性`
### 社交登陸
`用戶Google提供授權 -> 用戶協帶Grant向伺服器請求 -> 使用grant獲取用戶相關資料 -> 儲存至資料庫 `
<img width="385" height="231"  src="https://miro.medium.com/max/1225/1*SEt7MeJZP3Hxioirj4VMuQ.png" >
```java 
@GetMapping("/oauth/google/login")
    public String oauthlogin(){
        Map<String,String> param = new HashMap<>();
        param.put("scope",AuthConstants.GOOGLE_API_CERTIFICATION_INFO_URL);
        param.put("access_type","offline");
        param.put("include_granted_scopes","true");
        param.put("response_type","code");
        param.put("redirect_uri",AuthConstants.REDIRECT_LONIN_URL);
        param.put("client_id",constants.getClient_id());

        String url = URLs.buildUrl(AuthConstants.GOOGLE_API_CERTIFICATION_PAGE_URL,param);
        return "redirect:" +url;
    }
```
``` java
@GetMapping("/oauth/google/loginCallback")
    public String oauthcallback(String code,String scope,HttpSession session){
        try {
            GetTokenVO getTokenParam = new GetTokenVO();
            getTokenParam.setCode(code);
            getTokenParam.setClient_id(constants.getClient_id());
            getTokenParam.setClient_secret(constants.getClient_secret());
            getTokenParam.setGrant_type(constants.getGrant_type());
            getTokenParam.setRedirect_uri(AuthConstants.REDIRECT_LONIN_URL);
            HttpEntity<GetTokenVO> request = new HttpEntity<GetTokenVO>(getTokenParam);
            TokenVO vo = restTemplate.postForObject(AuthConstants.GOOGLE_GET_TOKEN_URL,request,TokenVO.class);
            GoogleUserInfoVO userInfoVO = restTemplate.getForObject(AuthConstants.GOOGLE_API_INFO_URL+"?access_token="+vo.getAccess_token() ,GoogleUserInfoVO.class);

            R r = memberFeignService.oauthLogin(userInfoVO);
            if(r.getCode() == 0){
                MemberEntityTO user = r.getData(new TypeReference<MemberEntityTO>() {
                });
                session.setAttribute(AuthConstants.USERKEY,user);
                return "redirect:http://gulimall.com/";
            }else {
                return "redirect:http://auth.gulimall.com/";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "redirect:http://auth.gulimall.com/";
        }
    }
```
### 用戶密碼存儲
`BCrypt`
```java
public MemberEntity regist(RegistAccountVO vo) {
        MemberEntity entity = new MemberEntity();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        entity.setPassword(encoder.encode(vo.getPassword()));
        save(entity);
        return entity;
    }
```
### 購物車
根據userid或visitorid在Redis中用map收集
* 訪客購物車 </br>
  用戶在沒登錄的情況下訪問購物車時，伺服器會判斷用戶端Session在Spring session中是否有登錄，如果沒有則會返回臨時的cookie(visitorid)，直到訪客登陸後再次訪問購物車，就會把visitorid對應的購物項資料加入到userid中。


### 影響請求速度的原因
###### 以下測試皆在`JMeter`與`VisualVM`中，且配置如下</br>
  ![image](https://github.com/day20180721/Guli/blob/dev/images/配置.png)
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
