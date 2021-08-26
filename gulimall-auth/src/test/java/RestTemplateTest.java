import com.littlejenny.gulimall.auth.vo.google.GetTokenVO;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

public class RestTemplateTest {
    @Test
    public void restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://auth.gulimall.com/t";
        GetTokenVO getTokenVO = new GetTokenVO();
        getTokenVO.setCode("555");
        HttpEntity<GetTokenVO> request = new HttpEntity<GetTokenVO>(getTokenVO);
        try {
            String s = restTemplate.postForObject(url,request,String.class);
            System.out.println(s);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
