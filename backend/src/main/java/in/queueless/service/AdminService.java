package in.queueless.service;

import in.queueless.api.ApiModels.*;
import in.queueless.model.Domain.*;
import in.queueless.repo.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminService {
 private final UserRepository users; private final OrganizationRepository orgs; private final QueueRepository queues; private final ServiceRepository services; private final StaffRepository staff;
 public AdminService(UserRepository users,OrganizationRepository orgs,QueueRepository queues,ServiceRepository services,StaffRepository staff){this.users=users;this.orgs=orgs;this.queues=queues;this.services=services;this.staff=staff;}

 @Transactional(readOnly=true) public AdminOverview overview(){return new AdminOverview(users.countByRole(Role.CUSTOMER),users.countByRole(Role.OWNER),orgs.count(),orgs.countByActiveTrue(),queues.count(),count(QueueStatus.WAITING),count(QueueStatus.SERVING),count(QueueStatus.COMPLETED),count(QueueStatus.CANCELLED),count(QueueStatus.NO_SHOW));}
 @Transactional(readOnly=true) public List<AdminCustomer> customers(){return users.findByRoleOrderByCreatedAtDesc(Role.CUSTOMER).stream().map(this::customer).toList();}
 @Transactional(readOnly=true) public AdminCustomerDetail customerDetail(Long id){User u=users.findById(id).filter(x->x.role==Role.CUSTOMER).orElseThrow(()->notFound("Customer not found"));return new AdminCustomerDetail(customer(u),queues.findByCustomerIdOrderByJoinedAtDesc(u.id).stream().map(this::appointment).toList());}
 @Transactional(readOnly=true) public List<AdminOrganization> organizations(){return orgs.findAll().stream().sorted(Comparator.comparing((Organization o)->o.createdAt).reversed()).map(this::organization).toList();}
 @Transactional(readOnly=true) public AdminOrganizationDetail organizationDetail(Long id){Organization o=orgs.findById(id).orElseThrow(()->notFound("Organization not found"));return new AdminOrganizationDetail(organization(o),queues.findByOrganizationIdOrderByJoinedAtDesc(id).stream().map(this::appointment).toList());}

 @Transactional public void deactivateCustomer(Long id){User u=users.findById(id).filter(x->x.role==Role.CUSTOMER).orElseThrow(()->notFound("Customer not found"));u.active=false;for(QueueEntry q:queues.findByCustomerIdOrderByJoinedAtDesc(id))cancelActive(q,"Account removed by master admin");}
 @Transactional public void deactivateOrganization(Long id){Organization org=orgs.findById(id).orElseThrow(()->notFound("Organization not found"));org.active=false;services.findByOrganizationIdAndActiveTrueOrderByNameAsc(id).forEach(s->s.active=false);staff.findByOrganizationIdAndActiveTrueOrderByNameAsc(id).forEach(s->s.active=false);for(QueueEntry q:queues.findByOrganizationIdOrderByJoinedAtDesc(id))cancelActive(q,"Organization removed by master admin");}
 @Transactional public void deactivateOwner(Long id){User owner=users.findById(id).filter(x->x.role==Role.OWNER).orElseThrow(()->notFound("Organization account not found"));owner.active=false;for(Organization org:orgs.findByOwnerIdOrderByNameAsc(id)){org.active=false;services.findByOrganizationIdAndActiveTrueOrderByNameAsc(org.id).forEach(s->s.active=false);staff.findByOrganizationIdAndActiveTrueOrderByNameAsc(org.id).forEach(s->s.active=false);for(QueueEntry q:queues.findByOrganizationIdOrderByJoinedAtDesc(org.id))cancelActive(q,"Organization account removed by master admin");}}

 private AdminCustomer customer(User u){return new AdminCustomer(u.id,u.name,u.email,u.profilePhoto,u.active,u.createdAt,queues.countByCustomerId(u.id),qc(u,QueueStatus.COMPLETED),qc(u,QueueStatus.CANCELLED),qc(u,QueueStatus.WAITING),qc(u,QueueStatus.SERVING),qc(u,QueueStatus.NO_SHOW));}
 private long qc(User u,QueueStatus s){return queues.countByCustomerIdAndStatus(u.id,s);}
 private AdminOrganization organization(Organization o){List<QueueEntry> entries=queues.findByOrganizationIdOrderByJoinedAtDesc(o.id);long uniqueCustomers=entries.stream().map(q->q.customer==null?"email:"+q.customerEmail:"id:"+q.customer.id).distinct().count();return new AdminOrganization(o.id,o.name,o.category,o.address,o.city,o.profilePhoto,o.active,o.createdAt,o.owner.id,o.owner.name,o.owner.email,uniqueCustomers,entries.size(),count(entries,QueueStatus.COMPLETED),count(entries,QueueStatus.CANCELLED),count(entries,QueueStatus.WAITING),count(entries,QueueStatus.SERVING),count(entries,QueueStatus.NO_SHOW));}
 private long count(List<QueueEntry> entries,QueueStatus status){return entries.stream().filter(q->q.status==status).count();}
 private AdminAppointment appointment(QueueEntry q){long duration=q.service==null?0:q.service.durationMinutes;return new AdminAppointment(q.id,q.visitDate,q.status.name(),q.queueNumber,q.service==null?"Service removed":q.service.name,q.organization==null?"Organization removed":q.organization.name,q.customerName,q.customerEmail,q.staff==null?null:q.staff.name,estimated(q,duration),q.cancellationReason,q.joinedAt,q.completedAt);}
 private Long estimated(QueueEntry q,long duration){if(q.status!=QueueStatus.WAITING)return 0L;List<QueueEntry> earlier=queues.findByOrganizationIdAndVisitDateOrderByQueueNumberAsc(q.organization.id,q.visitDate);long minutes=0;for(QueueEntry before:earlier){if(before.queueNumber>=q.queueNumber)break;if(before.status==QueueStatus.WAITING)minutes+=before.service.durationMinutes;else if(before.status==QueueStatus.SERVING&&before.startedAt!=null)minutes+=Math.max(0,before.service.durationMinutes-java.time.Duration.between(before.startedAt,java.time.Instant.now()).toMinutes());}return minutes;}
 private long count(QueueStatus s){return queues.countByStatus(s);}
 private void cancelActive(QueueEntry q,String reason){if(q.status==QueueStatus.WAITING||q.status==QueueStatus.SERVING){q.status=QueueStatus.CANCELLED;q.cancellationReason=reason;}}
 private ResponseStatusException notFound(String message){return new ResponseStatusException(HttpStatus.NOT_FOUND,message);}
}
