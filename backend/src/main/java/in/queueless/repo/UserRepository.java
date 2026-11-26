package in.queueless.repo;
import in.queueless.model.Domain.User;
import in.queueless.model.Domain.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User,Long> {
 Optional<User> findByEmailIgnoreCase(String email);
 boolean existsByEmailIgnoreCase(String email);
 Optional<User> findByEmailIgnoreCaseAndActiveTrue(String email);
 List<User> findByRoleOrderByCreatedAtDesc(Role role);
 long countByRole(Role role);
 long countByRoleAndActiveTrue(Role role);
 long countByRoleAndActiveFalse(Role role);
}
