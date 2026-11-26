package in.queueless.api;

import in.queueless.api.ApiModels.*;
import in.queueless.service.AdminService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/admin")
public class AdminController {
 private final AdminService admin;
 public AdminController(AdminService admin){this.admin=admin;}
 @GetMapping("/overview") public AdminOverview overview(){return admin.overview();}
 @GetMapping("/customers") public List<AdminCustomer> customers(){return admin.customers();}
 @GetMapping("/customers/{id}") public AdminCustomerDetail customer(@PathVariable Long id){return admin.customerDetail(id);}
 @DeleteMapping("/customers/{id}") public ResponseEntity<Void> removeCustomer(@PathVariable Long id){admin.deactivateCustomer(id);return ResponseEntity.noContent().build();}
 @GetMapping("/organizations") public List<AdminOrganization> organizations(){return admin.organizations();}
 @GetMapping("/organizations/{id}") public AdminOrganizationDetail organization(@PathVariable Long id){return admin.organizationDetail(id);}
 @DeleteMapping("/organizations/{id}") public ResponseEntity<Void> removeOrganization(@PathVariable Long id){admin.deactivateOrganization(id);return ResponseEntity.noContent().build();}
 @DeleteMapping("/organization-accounts/{id}") public ResponseEntity<Void> removeOrganizationAccount(@PathVariable Long id){admin.deactivateOwner(id);return ResponseEntity.noContent().build();}
}
