import com.google.api.client.extensions.java6.auth.oauth2.AbstractPromptReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.util.Throwables;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class MyVerificationCodeReceiver extends AbstractPromptReceiver {

    @Override
    public String getRedirectUri() throws IOException {
        return "http://auth.gulimall.com/oauth/google/login";
    }
}
