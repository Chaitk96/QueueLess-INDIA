package in.queueless.repo;
import in.queueless.model.Domain.Organization;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
public interface OrganizationRepository extends JpaRepository<Organization,Long> {
 List<Organization> findByActiveTrueOrderByNameAsc();
 List<Organization> findByActiveTrueAndCityContainingIgnoreCaseOrderByNameAsc(String city);
 Optional<Organization> findByIdAndActiveTrue(Long id);
 Optional<Organization> findByIdAndOwnerId(Long id,Long ownerId);
 List<Organization> findByOwnerIdOrderByNameAsc(Long ownerId);
 @Override @Lock(LockModeType.PESSIMISTIC_WRITE) Optional<Organization> findById(Long id);
}
