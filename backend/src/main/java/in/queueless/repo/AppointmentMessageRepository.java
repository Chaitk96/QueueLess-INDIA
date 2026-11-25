package in.queueless.repo;

import in.queueless.model.Domain.AppointmentMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentMessageRepository extends JpaRepository<AppointmentMessage,Long> {
    List<AppointmentMessage> findByQueueEntryIdOrderByCreatedAtAsc(Long queueEntryId);
}
