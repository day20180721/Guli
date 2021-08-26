import com.littlejenny.gulimall.auth.GuliAuth15000Main;
import com.littlejenny.gulimall.auth.constants.AuthConstants;
import com.littlejenny.gulimall.auth.utils.URLs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GuliAuth15000Main.class})
public class LoginURLBuildTest {

    @Autowired
    AuthConstants constants;
    @Test
    public void t(){
        Map<String,String> param = new HashMap<>();
        param.put("scope",AuthConstants.GOOGLE_API_CERTIFICATION_INFO_URL);
        param.put("access_type","offline");
        param.put("include_granted_scopes","true");
        param.put("response_type","code");
        param.put("redirect_uri",AuthConstants.REDIRECT_LONIN_URL);
        param.put("client_id",constants.getClient_id());

        String url = URLs.buildUrl(AuthConstants.GOOGLE_API_CERTIFICATION_PAGE_URL,param);
        System.out.println(url);
    }
}
