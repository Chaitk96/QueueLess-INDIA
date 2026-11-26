package in.queueless.api;

import in.queueless.model.Domain.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;

public final class ApiModels {
 private ApiModels(){}
 public record RegisterRequest(@NotBlank @Size(max=100) String name,@NotBlank @Email @Size(max=254) String email,@NotBlank @Size(min=8,max=72) String password,@Size(max=450000) String profilePhoto){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 public record UpdateProfileRequest(@NotBlank @Size(max=100) String name,@NotBlank @Email @Size(max=254) String email,@Size(max=450000) String profilePhoto){}
 public record AuthResponse(String accessToken,String tokenType,long expiresIn,AccountResponse account){}
 public record AccountResponse(Long id,String name,String email,String role,Instant freeAccessUntil,Instant subscriptionUntil,String subscriptionPlan,String profilePhoto){}
 public record OrganizationRequest(@NotBlank @Size(max=140) String name,@NotBlank @Size(max=140) String category,@NotBlank @Size(max=180) String address,@NotBlank @Size(max=80) String city,@Size(max=1000) String description,@Size(max=450000) String profilePhoto){}
 public record UpdateOrganizationRequest(@NotBlank @Size(max=140) String name,@NotBlank @Size(max=140) String category,@NotBlank @Size(max=180) String address,@NotBlank @Size(max=80) String city,@Size(max=1000) String description,@Size(max=450000) String profilePhoto){}
 public record OrganizationResponse(Long id,String name,String category,String address,String city,String description,long liveWaitMinutes,long peopleWaiting,String nextEstimatedTime,String profilePhoto){}
 public record ServiceRequest(@NotBlank @Size(max=120) String name,@Min(1) @Max(240) int durationMinutes){}
 public record ServiceResponse(Long id,String name,int durationMinutes){}
 public record StaffRequest(@NotBlank @Size(max=100) String name,@Size(max=120) String title){}
 public record StaffResponse(Long id,String name,String title){}
 public record JoinQueueRequest(@NotNull Long serviceId,@NotBlank @Size(max=100) String customerName,@NotBlank @Email @Size(max=254) String customerEmail,LocalDate visitDate){}
 public record QueueResponse(Long id,Long organizationId,String organizationName,String organizationCategory,String address,String city,String trackingCode,int queueNumber,String status,String customerName,String customerEmail,LocalDate visitDate,Instant joinedAt,Long serviceId,String serviceName,int peopleAhead,long estimatedWaitMinutes,String estimatedStartTime,Long staffId,String staffName,Instant startedAt,Instant completedAt,String cancellationReason,Integer rating,String reviewComment,String organizationProfilePhoto,String customerProfilePhoto){}
 public record ReviewRequest(@Min(1) @Max(5) int rating,@Size(max=1000) String comment){}
 public record ReviewResponse(Long id,String customerName,int rating,String comment,Instant createdAt){}
 public record RatingSummary(double average,int count,java.util.List<ReviewResponse> reviews){}
 public record AssignStaffRequest(@NotNull Long staffId){}
 public record CancelQueueRequest(@NotBlank @Size(max=500) String reason){}
 public record AppointmentMessageRequest(@NotBlank @Size(max=1000) String body){}
 public record AppointmentMessageResponse(Long id,String authorName,String authorRole,String body,Instant createdAt){}
 public record MessageResponse(String message){}
 public record CreatePaymentOrderRequest(@NotBlank String planCode){}
 public record PaymentOrderResponse(String keyId,String orderId,int amount,String currency,String planCode,int durationDays,String description){}
 public record VerifyPaymentRequest(@NotBlank String razorpayOrderId,@NotBlank String razorpayPaymentId,@NotBlank String razorpaySignature){}
 public record SubscriptionResponse(String status,String planCode,Instant freeAccessUntil,Instant subscriptionUntil,Instant accessUntil){}
 public record AdminOverview(long customers,long owners,long organizations,long activeOrganizations,long appointments,long waiting,long serving,long completed,long cancelled,long noShow){}
 public record AdminCustomer(Long id,String name,String email,String profilePhoto,boolean active,Instant registeredAt,long appointments,long completed,long cancelled,long waiting,long serving,long noShow){}
 public record AdminOrganization(Long id,String name,String category,String address,String city,String profilePhoto,boolean active,Instant registeredAt,Long ownerId,String ownerName,String ownerEmail,long registeredCustomers,long appointments,long completed,long cancelled,long waiting,long serving,long noShow){}
 public record AdminAppointment(Long id,LocalDate visitDate,String status,int queueNumber,String serviceName,String organizationName,String customerName,String customerEmail,String staffName,Long estimatedWaitMinutes,String cancellationReason,Instant joinedAt,Instant completedAt){}
 public record AdminCustomerDetail(AdminCustomer customer,java.util.List<AdminAppointment> appointments){}
 public record AdminOrganizationDetail(AdminOrganization organization,java.util.List<AdminAppointment> appointments){}
 public static AccountResponse account(User u){return new AccountResponse(u.id,u.name,u.email,u.role.name(),u.freeAccessUntil,u.subscriptionUntil,u.subscriptionPlan,u.profilePhoto);}
 public static OrganizationResponse organization(Organization o,long wait,long waiting,String nextEstimatedTime){return new OrganizationResponse(o.id,o.name,o.category,o.address,o.city,o.description,wait,waiting,nextEstimatedTime,o.profilePhoto);}
 public static ServiceResponse service(ServiceItem s){return new ServiceResponse(s.id,s.name,s.durationMinutes);}
 public static StaffResponse staff(StaffMember s){return new StaffResponse(s.id,s.name,s.title);}
}
