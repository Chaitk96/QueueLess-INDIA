package in.queueless.api;
import in.queueless.api.ApiModels.*;
import in.queueless.model.Domain.QueueStatus;
import in.queueless.service.QueueService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1")
public class QueueController {
 private final QueueService queue;public QueueController(QueueService queue){this.queue=queue;}
 @PostMapping("/queues/join") public QueueResponse join(@Valid @RequestBody JoinQueueRequest r,Authentication a){return queue.join(r,a.getName());}
 @PostMapping("/queues/me/join") public QueueResponse joinMine(@Valid @RequestBody JoinQueueRequest r,Authentication a){return queue.join(r,a.getName());}
 @GetMapping("/queues/track/{code}") public QueueResponse track(@PathVariable String code,@RequestParam String email){return queue.track(code,email);}
 @DeleteMapping("/queues/track/{code}") public QueueResponse cancel(@PathVariable String code,@RequestParam String email,@RequestParam(defaultValue="Cancelled by customer") String reason){return queue.cancelByCode(code,email,reason);}
 @GetMapping("/queues/me") public List<QueueResponse> myQueues(Authentication a){return queue.myQueues(a.getName());}
 @GetMapping("/queues/me/history") public List<QueueResponse> myHistory(Authentication a){return queue.myHistory(a.getName());}
 @PostMapping("/queues/me/{entryId}/review") public QueueResponse review(@PathVariable Long entryId,@Valid @RequestBody ReviewRequest r,Authentication a){return queue.review(entryId,r.rating(),r.comment(),a.getName());}
 @GetMapping("/organizations/{orgId}/ratings") public RatingSummary ratings(@PathVariable Long orgId){return queue.ratings(orgId);}
 @PostMapping("/queues/me/{entryId}/cancel") public QueueResponse cancelMine(@PathVariable Long entryId,@Valid @RequestBody CancelQueueRequest r,Authentication a){return queue.cancelByCustomer(entryId,r.reason(),a.getName());}
 @GetMapping("/queues/me/{entryId}/messages") public List<AppointmentMessageResponse> customerMessages(@PathVariable Long entryId,Authentication a){return queue.messagesForCustomer(entryId,a.getName());}
 @PostMapping("/queues/me/{entryId}/messages") public AppointmentMessageResponse customerMessage(@PathVariable Long entryId,@Valid @RequestBody AppointmentMessageRequest r,Authentication a){return queue.sendCustomerMessage(entryId,r.body(),a.getName());}
 @GetMapping("/owner/organizations/{orgId}/queue") public List<QueueResponse> live(@PathVariable Long orgId,@RequestParam(defaultValue="") String date,Authentication a){return queue.live(orgId,date.isBlank()?LocalDate.now():LocalDate.parse(date),a.getName());}
 @GetMapping("/owner/organizations/{orgId}/history") public List<QueueResponse> history(@PathVariable Long orgId,Authentication a){return queue.ownerHistory(orgId,a.getName());}
 @GetMapping("/owner/organizations/{orgId}/queue/{entryId}/messages") public List<AppointmentMessageResponse> ownerMessages(@PathVariable Long orgId,@PathVariable Long entryId,Authentication a){return queue.messagesForOwner(orgId,entryId,a.getName());}
 @PostMapping("/owner/organizations/{orgId}/queue/{entryId}/messages") public AppointmentMessageResponse ownerMessage(@PathVariable Long orgId,@PathVariable Long entryId,@Valid @RequestBody AppointmentMessageRequest r,Authentication a){return queue.sendOwnerMessage(orgId,entryId,r.body(),a.getName());}
 @PostMapping("/owner/organizations/{orgId}/queue/{entryId}/call") public QueueResponse call(@PathVariable Long orgId,@PathVariable Long entryId,Authentication a){return queue.update(orgId,entryId,QueueStatus.SERVING,a.getName());}
 @PostMapping("/owner/organizations/{orgId}/queue/{entryId}/complete") public QueueResponse complete(@PathVariable Long orgId,@PathVariable Long entryId,Authentication a){return queue.update(orgId,entryId,QueueStatus.COMPLETED,a.getName());}
 @PostMapping("/owner/organizations/{orgId}/queue/{entryId}/no-show") public QueueResponse noShow(@PathVariable Long orgId,@PathVariable Long entryId,Authentication a){return queue.update(orgId,entryId,QueueStatus.NO_SHOW,a.getName());}
 @PostMapping("/owner/organizations/{orgId}/queue/{entryId}/cancel") public QueueResponse cancelByOwner(@PathVariable Long orgId,@PathVariable Long entryId,Authentication a){return queue.cancelByOwner(orgId,entryId,a.getName());}
 @PutMapping("/owner/organizations/{orgId}/queue/{entryId}/staff") public QueueResponse assign(@PathVariable Long orgId,@PathVariable Long entryId,@Valid @RequestBody AssignStaffRequest r,Authentication a){return queue.assign(orgId,entryId,r.staffId(),a.getName());}
}
