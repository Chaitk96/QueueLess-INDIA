package in.queueless.service;

import in.queueless.api.ApiModels.*;
import in.queueless.api.ApiModels;
import in.queueless.model.Domain.*;
import in.queueless.repo.*;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrganizationService {
 private final OrganizationRepository orgs;private final ServiceRepository services;private final StaffRepository staff;private final QueueRepository queues;private final UserRepository users;
 public OrganizationService(OrganizationRepository o,ServiceRepository s,StaffRepository st,QueueRepository q,UserRepository u){orgs=o;services=s;staff=st;queues=q;users=u;}
 public List<OrganizationResponse> list(String city){List<Organization> list=(city==null||city.isBlank())?orgs.findByActiveTrueOrderByNameAsc():orgs.findByActiveTrueAndCityContainingIgnoreCaseOrderByNameAsc(city.trim());return list.stream().map(o->publicCard(o,java.time.LocalDate.now(java.time.ZoneId.of("Asia/Kolkata")))).toList();}
 @Transactional(readOnly=true) public OrganizationResponse get(Long id,java.time.LocalDate visitDate){Organization o=orgs.findByIdAndActiveTrue(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Organization not found"));java.time.LocalDate today=java.time.LocalDate.now(java.time.ZoneId.of("Asia/Kolkata"));java.time.LocalDate date=visitDate==null?today:visitDate;if(date.isBefore(today)||date.isAfter(today.plusDays(30)))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Visit date must be within the next 30 days");return publicCard(o,date);}
 private OrganizationResponse publicCard(Organization o,java.time.LocalDate date){List<QueueEntry> active=queues.findByOrganizationIdAndVisitDateAndStatusInOrderByQueueNumberAsc(o.id,date,List.of(QueueStatus.WAITING,QueueStatus.SERVING));long wait=0;for(QueueEntry q:active){if(q.status==QueueStatus.WAITING)wait+=q.service.durationMinutes;else if(q.startedAt!=null)wait+=Math.max(0,q.service.durationMinutes-java.time.Duration.between(q.startedAt,java.time.Instant.now()).toMinutes());}java.time.ZoneId india=java.time.ZoneId.of("Asia/Kolkata");java.time.LocalTime ten=java.time.LocalTime.of(10,0);java.time.LocalTime base=date.equals(java.time.LocalDate.now(india))&&java.time.LocalTime.now(india).isAfter(ten)?java.time.LocalTime.now(india).withSecond(0).withNano(0):ten;String next=base.plusMinutes(wait).format(java.time.format.DateTimeFormatter.ofPattern("h:mm a",java.util.Locale.ENGLISH));return ApiModels.organization(o,wait,active.size(),next);}
 @Transactional public OrganizationResponse create(OrganizationRequest r,String ownerEmail){User owner=user(ownerEmail);AccessPolicy.requireAccess(owner);Organization o=new Organization(r.name().trim(),r.category().trim(),r.address().trim(),r.city().trim(),r.description(),owner);o.profilePhoto=photo(r.profilePhoto());o=orgs.save(o);return ApiModels.organization(o,0,0,"10:00 AM");}
 @Transactional public OrganizationResponse update(Long id,UpdateOrganizationRequest r,String email){Organization o=owned(id,email);o.name=r.name().trim();o.category=r.category().trim();o.address=r.address().trim();o.city=r.city().trim();o.description=r.description();o.profilePhoto=photo(r.profilePhoto());return publicCard(o,java.time.LocalDate.now(java.time.ZoneId.of("Asia/Kolkata")));}
 @Transactional(readOnly=true) public List<OrganizationResponse> mine(String email){User owner=user(email);java.time.LocalDate today=java.time.LocalDate.now(java.time.ZoneId.of("Asia/Kolkata"));return orgs.findByOwnerIdOrderByNameAsc(owner.id).stream().map(o->publicCard(o,today)).toList();}
 @Transactional public ServiceResponse addService(Long orgId,ServiceRequest r,String email){Organization o=owned(orgId,email);return ApiModels.service(services.save(new ServiceItem(o,r.name().trim(),r.durationMinutes())));}
 @Transactional(readOnly=true) public List<ServiceResponse> services(Long id){if(!orgs.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Organization not found");return services.findByOrganizationIdAndActiveTrueOrderByNameAsc(id).stream().map(ApiModels::service).toList();}
 @Transactional public void removeService(Long orgId,Long serviceId,String email){owned(orgId,email);ServiceItem item=services.findByIdAndOrganization_IdAndActiveTrue(serviceId,orgId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Service not found"));item.active=false;}
 @Transactional public StaffResponse addStaff(Long orgId,StaffRequest r,String email){Organization o=owned(orgId,email);return ApiModels.staff(staff.save(new StaffMember(o,r.name().trim(),r.title())));}
 @Transactional(readOnly=true) public List<StaffResponse> staff(Long id){if(!orgs.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Organization not found");return staff.findByOrganizationIdAndActiveTrueOrderByNameAsc(id).stream().map(ApiModels::staff).toList();}
 @Transactional public void removeStaff(Long orgId,Long staffId,String email){owned(orgId,email);StaffMember item=staff.findByIdAndOrganization_IdAndActiveTrue(staffId,orgId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Team member not found"));item.active=false;}
 public Organization owned(Long id,String email){User u=user(email);AccessPolicy.requireAccess(u);return orgs.findByIdAndOwnerId(id,u.id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Organization not found for this account"));}
 private User user(String email){return users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found"));}
 private String photo(String value){if(value==null||value.isBlank())return null;if(value.length()>450000||!value.matches("^data:image/(jpeg|png|webp);base64,[A-Za-z0-9+/]+=*$"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Profile photo must be a JPEG, PNG, or WebP image under 320 KB");return value;}
}
