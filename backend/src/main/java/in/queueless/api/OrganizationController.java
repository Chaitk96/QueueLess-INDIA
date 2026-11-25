package in.queueless.api;
import in.queueless.api.ApiModels.*;
import in.queueless.service.OrganizationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1")
public class OrganizationController {
 private final OrganizationService service;public OrganizationController(OrganizationService service){this.service=service;}
 @GetMapping("/organizations") public List<OrganizationResponse> list(@RequestParam(required=false) String city){return service.list(city);}
 @GetMapping("/organizations/{id}") public OrganizationResponse get(@PathVariable Long id,@RequestParam(required=false) java.time.LocalDate date){return service.get(id,date);}
 @GetMapping("/organizations/{id}/services") public List<ServiceResponse> services(@PathVariable Long id){return service.services(id);}
 @GetMapping("/organizations/{id}/staff") public List<StaffResponse> staff(@PathVariable Long id){return service.staff(id);}
 @GetMapping("/owner/organizations") public List<OrganizationResponse> mine(Authentication a){return service.mine(a.getName());}
 @PostMapping("/owner/organizations") public OrganizationResponse create(@Valid @RequestBody OrganizationRequest r,Authentication a){return service.create(r,a.getName());}
 @PostMapping("/owner/organizations/{id}/services") public ServiceResponse addService(@PathVariable Long id,@Valid @RequestBody ServiceRequest r,Authentication a){return service.addService(id,r,a.getName());}
 @PostMapping("/owner/organizations/{id}/staff") public StaffResponse addStaff(@PathVariable Long id,@Valid @RequestBody StaffRequest r,Authentication a){return service.addStaff(id,r,a.getName());}
 @DeleteMapping("/owner/organizations/{id}/services/{serviceId}") public MessageResponse removeService(@PathVariable Long id,@PathVariable Long serviceId,Authentication a){service.removeService(id,serviceId,a.getName());return new MessageResponse("Service removed");}
 @DeleteMapping("/owner/organizations/{id}/staff/{staffId}") public MessageResponse removeStaff(@PathVariable Long id,@PathVariable Long staffId,Authentication a){service.removeStaff(id,staffId,a.getName());return new MessageResponse("Team member removed");}
}
