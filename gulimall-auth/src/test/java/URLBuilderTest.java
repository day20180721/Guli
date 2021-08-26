import java.util.HashMap;
import java.util.Map;

public class URLBuilderTest {
    public static void main(String[] args) {
        String url = "https://accounts.google.com/o/oauth2/v2/auth";
        Map<String,String> map = new HashMap<>();
        map.put("GG","G");
        map.put("DD","D");
        map.put("AA","A");
        System.out.println(buildUrl(url,map));
    }
    private static String buildUrl(String url, Map<String,String> map){
        StringBuffer buffer = new StringBuffer();
        buffer.append(url);
        buffer.append("?");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            buffer.append(entry.getKey() + "="+entry.getValue()+"&");
        }
        buffer.deleteCharAt(buffer.capacity() - 1);
        return buffer.toString();
    }
}
