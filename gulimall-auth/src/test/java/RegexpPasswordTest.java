import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RegexpPasswordTest {
    public static void main(String[] args) {
        String password = "$2a$10$16OZ0/GVT4x9IKhoZmdT0e3SwN7lMcBX7xKqVG4BYzy4Y/QdN7S.O";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean developer = encoder.matches("developer", password);
        System.out.println(developer);
    }
}
