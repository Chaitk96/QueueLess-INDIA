package in.queueless.api;

import in.queueless.api.ApiModels.CreatePaymentOrderRequest;
import in.queueless.api.ApiModels.PaymentOrderResponse;
import in.queueless.api.ApiModels.SubscriptionResponse;
import in.queueless.api.ApiModels.VerifyPaymentRequest;
import in.queueless.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/billing")
public class BillingController {
    private final BillingService billing;
    public BillingController(BillingService billing){this.billing=billing;}
    @PostMapping("/orders") public PaymentOrderResponse order(@Valid @RequestBody CreatePaymentOrderRequest request,Authentication auth){return billing.createOrder(auth.getName(),request.planCode());}
    @PostMapping("/verify") public SubscriptionResponse verify(@Valid @RequestBody VerifyPaymentRequest request,Authentication auth){return billing.verify(auth.getName(),request);}
}
