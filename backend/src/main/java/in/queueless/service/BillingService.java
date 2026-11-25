package in.queueless.service;

import in.queueless.api.ApiModels.*;
import in.queueless.model.Domain.*;
import in.queueless.repo.PaymentOrderRepository;
import in.queueless.repo.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BillingService {
    private record Plan(String code,int amountPaise,int days,String label,Role role){}
    private record RazorpayOrder(String id,int amount,String currency,String status){}
    private record RazorpayPayment(String id,String order_id,int amount,String currency,String status){}
    private final UserRepository users;private final PaymentOrderRepository orders;private final String keyId;private final String keySecret;

    public BillingService(UserRepository users,PaymentOrderRepository orders,@Value("${billing.razorpay.key-id:}") String keyId,@Value("${billing.razorpay.key-secret:}") String keySecret){this.users=users;this.orders=orders;this.keyId=keyId;this.keySecret=keySecret;}

    @Transactional public PaymentOrderResponse createOrder(String email,String planCode){User user=user(email);Plan plan=plan(planCode,user.role);requireConfigured();String receipt="ql-"+UUID.randomUUID().toString().replace("-","").substring(0,32);RazorpayOrder remote;
        try{remote=client().post().uri("/orders").body(Map.of("amount",plan.amountPaise(),"currency","INR","receipt",receipt,"notes",Map.of("userId",user.id,"planCode",plan.code()))).retrieve().body(RazorpayOrder.class);}catch(RestClientException e){throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"The payment service could not start checkout. Please try again later or contact support@queuelessindia.com.");}
        if(remote==null||remote.id()==null||remote.amount()!=plan.amountPaise()||!"INR".equals(remote.currency()))throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Razorpay returned an invalid order.");orders.save(new PaymentOrder(user,remote.id(),plan.code(),plan.amountPaise(),plan.days()));return new PaymentOrderResponse(keyId,remote.id(),plan.amountPaise(),"INR",plan.code(),plan.days(),plan.label());}

    @Transactional public SubscriptionResponse verify(String email,VerifyPaymentRequest request){User user=user(email);PaymentOrder order=orders.findByRazorpayOrderIdAndUser_Id(request.razorpayOrderId(),user.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Payment order not found for this account"));if("PAID".equals(order.status)){if(request.razorpayPaymentId().equals(order.paymentId))return subscription(user);throw new ResponseStatusException(HttpStatus.CONFLICT,"This payment order has already been used");}requireConfigured();String expected=signature(order.razorpayOrderId+"|"+request.razorpayPaymentId());if(!MessageDigest.isEqual(expected.getBytes(StandardCharsets.US_ASCII),request.razorpaySignature().getBytes(StandardCharsets.US_ASCII)))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Razorpay signature verification failed");RazorpayPayment payment;
        try{payment=client().get().uri("/payments/{id}",request.razorpayPaymentId()).retrieve().body(RazorpayPayment.class);if(payment!=null&&"authorized".equals(payment.status()))payment=client().post().uri("/payments/{id}/capture",request.razorpayPaymentId()).body(Map.of("amount",order.amountPaise,"currency","INR")).retrieve().body(RazorpayPayment.class);}catch(RestClientException e){throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Razorpay payment could not be confirmed yet. Please retry shortly.");}
        if(payment==null||!request.razorpayPaymentId().equals(payment.id())||!order.razorpayOrderId.equals(payment.order_id())||payment.amount()!=order.amountPaise||!"INR".equals(payment.currency())||!"captured".equals(payment.status()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Payment is not captured yet. Your access was not changed.");Plan plan=plan(order.planCode,user.role);Instant start=Instant.now();Instant trial=user.freeAccessUntil;if(trial!=null&&trial.isAfter(start))start=trial;if(user.subscriptionUntil!=null&&user.subscriptionUntil.isAfter(start))start=user.subscriptionUntil;user.subscriptionUntil=start.plus(plan.days(),ChronoUnit.DAYS);user.subscriptionPlan=plan.code();order.status="PAID";order.paymentId=payment.id();return subscription(user);}

    private SubscriptionResponse subscription(User user){Instant access=AccessPolicy.accessUntil(user);return new SubscriptionResponse("ACTIVE",user.subscriptionPlan,user.freeAccessUntil,user.subscriptionUntil,access);}
    private User user(String email){return users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found"));}
    private Plan plan(String code,Role role){Plan p=switch(code){case "CUSTOMER_7_DAY"->new Plan(code,900,7,"7-day QueueLess pass",Role.CUSTOMER);case "CUSTOMER_30_DAY"->new Plan(code,4900,30,"30-day QueueLess pass",Role.CUSTOMER);case "CUSTOMER_YEAR"->new Plan(code,9900,365,"QueueLess Black Friday yearly pass",Role.CUSTOMER);case "ORGANIZATION_MONTH"->new Plan(code,199900,30,"QueueLess organization monthly plan",Role.OWNER);default->throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown subscription plan");};if(role!=p.role())throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This plan is not available for this account type");return p;}
    private void requireConfigured(){if(keyId.isBlank()||keySecret.isBlank())throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Secure checkout is temporarily unavailable. Please try again later or contact support@queuelessindia.com.");}
    private RestClient client(){return RestClient.builder().baseUrl("https://api.razorpay.com/v1").defaultHeaders(h->h.setBasicAuth(keyId,keySecret)).build();}
    private String signature(String message){try{Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));return HexFormat.of().formatHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException("Unable to verify payment signature",e);}}
}
