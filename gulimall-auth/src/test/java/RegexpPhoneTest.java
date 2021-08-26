import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RegexpPhoneTest {
    public static void main(String[] args) {
        String phone = "0832987710";
        boolean matches = phone.matches("^(09)\\d{8}$");
        System.out.println(matches);

    }
}
