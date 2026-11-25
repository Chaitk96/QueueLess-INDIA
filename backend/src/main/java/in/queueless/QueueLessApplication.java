package in.queueless;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import java.util.TimeZone;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class QueueLessApplication {
    public static void main(String[] args) { TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata")); SpringApplication.run(QueueLessApplication.class, args); }
}
