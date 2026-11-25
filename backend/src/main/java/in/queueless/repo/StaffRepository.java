package in.queueless.repo;
import in.queueless.model.Domain.StaffMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StaffRepository extends JpaRepository<StaffMember,Long> {
 List<StaffMember> findByOrganizationIdAndActiveTrueOrderByNameAsc(Long organizationId);
 Optional<StaffMember> findByIdAndOrganization_IdAndActiveTrue(Long id,Long organizationId);
}
