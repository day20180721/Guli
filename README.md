# Guli 
###### Guli為教學項目，來源於[此](https://www.youtube.com/playlist?list=PLmOn9nNkQxJEwPjhNwGliP_bw3RjkgFCf)
* gulimall.com 首頁
* search.gulimall.com 搜尋頁面
* item.gulimall.com 商品詳情頁
* cart.gulimall.com 購物車
* order.gulimall.com 結帳頁面
* auth.gulimall.com 登錄註冊頁
### 自動生成基礎代碼
提供資料表給renren-generator，會生成對應該表的CRUD(Controller、Service、Dao)及前端Vue介面(基於renren-fast)
### SQL表冗餘字段
![image](https://user-images.githubusercontent.com/44454920/134792882-a6e4df8b-c6e8-46b0-887a-48897222581f.png)</br>
多對多的中間表，經常會透過其中一方的ID來查詢相對應的表資料，我們將很常被搜尋的表資料額外儲存在中間表，就能減少SQL的連接次數，以空間換取時間
### 社交登陸
`用戶Google提供授權 -> 用戶協帶Grant向伺服器請求 -> 使用grant獲取用戶相關資料 -> 儲存至資料庫 `
<img width="50%" height="50%"  src="https://miro.medium.com/max/1225/1*SEt7MeJZP3Hxioirj4VMuQ.png" >
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
根據 **user-Id** 或 **visitor-Id** 在Redis中用map收集
* 訪客購物車 </br>
  用戶在沒登錄的情況下訪問購物車時，伺服器會判斷用戶端Session在Spring session中是否有登錄，如果沒有則會返回臨時的cookie(visitor-Id)，直到訪客登陸後再次訪問購物車，就會把 visitor-Id 對應的購物項資料加入到 user-Id 中。

### 秒殺
面對高併發的流量，我們需要在最短時間內處理完請求，所以處理一個請求的過程為</br>
`判斷是否已經購買 -> 請求Redis信號量 --成功-> 發送購買成功的Rabbitmq --> 達到最終一致性`</br>
Redis + Rabbitmq
![image](https://user-images.githubusercontent.com/44454920/134794870-86418dab-5c38-4225-9f4a-2042f9d08c10.png)
### 秒殺活動自動更新
使用spring cronjob設定每隔一段時間就去資料庫查詢近期活動，並將關聯的商品訊息、數量信號量放到Redis中備用
為了避免集群重複執行，必須要用分布式鎖(Redisson)來區隔，並且在發布近期活動時保持冪等性，這裡使用的是在SQL中添加字段Status(1-上架/0-未上架)
```java
@Scheduled(cron = "*/30 * * * * ?")
```
### 秒殺活動開始才能搶購
每個秒殺商品都需要協帶token才能購買，此token只有在開始的時候才會顯示在html中
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
### 後續
* 用外網連接本地網路
  `註冊no ip -> 本地架設轉發中間件(spring mvc) -> 中間件向虛擬機的nginx發送請求 -> 將請求結果寫回Response`   
  ![image](https://user-images.githubusercontent.com/44454920/134864533-8ecee3c9-655c-494f-91d5-96b72b1c255b.png)
  ```java
  @GetMapping("/**")
    public void gulimall(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = "";
        String[] split = request.getServerName().split("-");
        if(split.length == 2){
            url  = "http://"+split[0]+".gulimall.com" + request.getServletPath();
        }else if(split.length == 1){
            url  = "http://gulimall.com" + request.getServletPath();
        }
        if("".equals(url)){
            return;
        }
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);

        ServletOutputStream outputStream = response.getOutputStream();
        InputStream inputStream = con.getInputStream();
        byte[] buffer = new byte[8192];
        int bytesum = 0;
        int byteread = 0;
        while ((byteread = inputStream.read(buffer)) != -1) {
            bytesum += byteread; // 字節 文件大小
            outputStream.write(buffer, 0, byteread);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }
  ```
* GitHub觸發Jenkins自動發布Docker鏡像
    ![image](https://user-images.githubusercontent.com/44454920/134865622-1bf8b89e-511d-4cee-96b4-27a2d3ca44af.png)
    <img width="50%" height="50%" src="https://user-images.githubusercontent.com/44454920/134867397-9a4e9aa7-211e-4124-b005-ed56f4241525.png" >
   
   ```java
    @PostMapping("/demo")
    @ResponseStatus(value = HttpStatus.OK)
    public void demo(HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String url = "http://192.168.30.32:8080/job/gulimalal-product-test/build";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //默认值我GET
        con.setRequestMethod("POST");
        //添加请求头
        int responseCode = con.getResponseCode();
    }
    ```


