package in.queueless.security;

import in.queueless.model.Domain.Role;
import in.queueless.model.Domain.User;
import in.queueless.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MasterAdminSeeder implements ApplicationRunner {
 private static final Logger log=LoggerFactory.getLogger(MasterAdminSeeder.class);
 private final UserRepository users; private final PasswordEncoder encoder; private final String email,password;
 public MasterAdminSeeder(UserRepository users,PasswordEncoder encoder,@Value("${app.master-admin.email:masteradmin@gmail.com}") String email,@Value("${app.master-admin.password:}") String password){this.users=users;this.encoder=encoder;this.email=email.trim().toLowerCase();this.password=password;}
 @Override @Transactional public void run(ApplicationArguments args){if(password==null||password.isBlank()){log.warn("Master admin is not initialized. Set MASTER_ADMIN_PASSWORD in the service environment.");return;}if(password.length()<8){throw new IllegalStateException("MASTER_ADMIN_PASSWORD must contain at least 8 characters");}var existing=users.findByEmailIgnoreCase(email);if(existing.isPresent()){User user=existing.get();if(user.role!=Role.MASTER_ADMIN)throw new IllegalStateException("Configured master admin email already belongs to a non-admin account");if(!user.active){user.active=true;log.info("Reactivated configured master admin account");}return;}User admin=new User("Master Admin",email,encoder.encode(password),Role.MASTER_ADMIN);users.save(admin);log.info("Master admin account initialized for {}",email);}
}
