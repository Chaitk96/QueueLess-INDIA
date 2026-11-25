package in.queueless.repo;

import in.queueless.model.Domain.PaymentOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long> {
    Optional<PaymentOrder> findByRazorpayOrderIdAndUser_Id(String razorpayOrderId,Long userId);
}
