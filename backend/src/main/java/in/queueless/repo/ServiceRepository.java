package in.queueless.repo;
import in.queueless.model.Domain.ServiceItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ServiceRepository extends JpaRepository<ServiceItem,Long> {
 List<ServiceItem> findByOrganizationIdAndActiveTrueOrderByNameAsc(Long organizationId);
 Optional<ServiceItem> findByIdAndOrganization_IdAndActiveTrue(Long id,Long organizationId);
}
