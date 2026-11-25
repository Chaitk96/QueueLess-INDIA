package in.queueless.repo;
import in.queueless.model.Domain.QueueEntry;
import in.queueless.model.Domain.QueueStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
public interface QueueRepository extends JpaRepository<QueueEntry,Long> {
 List<QueueEntry> findByOrganizationIdAndVisitDateOrderByQueueNumberAsc(Long organizationId,LocalDate date);
 List<QueueEntry> findByCustomerIdAndStatusInOrderByJoinedAtDesc(Long customerId,List<QueueStatus> statuses);
 List<QueueEntry> findByCustomerIdOrderByJoinedAtDesc(Long customerId);
 @Modifying @Query(value="UPDATE queue_entries SET customer_name=:name, customer_email=:email WHERE customer_id=:customerId",nativeQuery=true) int updateCustomerDetails(@Param("customerId") Long customerId,@Param("name") String name,@Param("email") String email);
 List<QueueEntry> findByOrganizationIdOrderByJoinedAtDesc(Long organizationId);
 List<QueueEntry> findByOrganizationIdAndVisitDateAndStatusInOrderByQueueNumberAsc(Long orgId,LocalDate date,List<QueueStatus> statuses);
 Optional<QueueEntry> findByIdAndOrganizationId(Long id,Long organizationId);
 Optional<QueueEntry> findByTrackingCodeAndCustomerEmailIgnoreCase(String code,String email);
 Optional<QueueEntry> findByIdAndCustomerId(Long id,Long customerId);
 long countByOrganizationIdAndVisitDateAndStatusIn(Long orgId,LocalDate date,List<QueueStatus> statuses);
 Optional<QueueEntry> findTopByOrganizationIdAndVisitDateOrderByQueueNumberDesc(Long orgId,LocalDate date);
 int countByOrganizationIdAndVisitDateAndQueueNumberLessThanAndStatusIn(Long orgId,LocalDate date,int number,List<QueueStatus> statuses);
}
