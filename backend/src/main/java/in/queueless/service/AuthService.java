package in.queueless.service;

import in.queueless.api.ApiModels.*;
import in.queueless.api.ApiModels;
import in.queueless.model.Domain.*;
import in.queueless.repo.UserRepository;
import in.queueless.repo.QueueRepository;
import in.queueless.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {
 private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt; private final QueueRepository queues;
 public AuthService(UserRepository users,PasswordEncoder encoder,JwtService jwt,QueueRepository queues){this.users=users;this.encoder=encoder;this.jwt=jwt;this.queues=queues;}
 @Transactional public AuthResponse register(RegisterRequest r,Role role){String email=r.email().trim().toLowerCase();if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists");User u=new User(r.name().trim(),email,encoder.encode(r.password()),role);u.profilePhoto=photo(r.profilePhoto());u.freeAccessUntil=Instant.now().plus(role==Role.CUSTOMER?Duration.ofDays(5):Duration.ofHours(2));return response(users.save(u));}
 @Transactional(readOnly=true) public AuthResponse login(LoginRequest r){User u=users.findByEmailIgnoreCaseAndActiveTrue(r.email().trim()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect"));if(!encoder.matches(r.password(),u.passwordHash))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect");return response(u);}
 @Transactional(readOnly=true) public AccountResponse current(String email){return ApiModels.account(users.findByEmailIgnoreCaseAndActiveTrue(email).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found or deactivated")));}
 @Transactional public AuthResponse updateProfile(String currentEmail,UpdateProfileRequest request){User u=users.findByEmailIgnoreCaseAndActiveTrue(currentEmail).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found"));String nextEmail=request.email().trim().toLowerCase();if(!u.email.equalsIgnoreCase(nextEmail)&&users.existsByEmailIgnoreCase(nextEmail))throw new ResponseStatusException(HttpStatus.CONFLICT,"That email is already linked to another account");u.name=request.name().trim();u.email=nextEmail;u.profilePhoto=photo(request.profilePhoto());queues.updateCustomerDetails(u.id,u.name,u.email);return response(u);}
 private String photo(String value){if(value==null||value.isBlank())return null;if(value.length()>450000||!value.matches("^data:image/(jpeg|png|webp);base64,[A-Za-z0-9+/]+=*$"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Profile photo must be a JPEG, PNG, or WebP image under 320 KB");return value;}
 private AuthResponse response(User u){return new AuthResponse(jwt.issue(u),"Bearer",jwt.ttlSeconds(),ApiModels.account(u));}
}
