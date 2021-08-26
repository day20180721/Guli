import com.littlejenny.gulimall.auth.GuliAuth15000Main;
import com.littlejenny.gulimall.auth.constants.AuthConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GuliAuth15000Main.class})
public class RedisTest {
    @Autowired
    private RedisTemplate<String, String> template;
    @Test
    public void redis(){
        System.out.println(template);
    }
    @Autowired
    AuthConstants constants;

}
