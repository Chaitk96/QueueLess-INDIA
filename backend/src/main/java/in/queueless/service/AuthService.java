package in.queueless.service;

import in.queueless.api.ApiModels.*;
import in.queueless.api.ApiModels;
import in.queueless.model.Domain.*;
import in.queueless.repo.UserRepository;
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
 private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
 public AuthService(UserRepository users,PasswordEncoder encoder,JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
 @Transactional public AuthResponse register(RegisterRequest r,Role role){String email=r.email().trim().toLowerCase();if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists");User u=new User(r.name().trim(),email,encoder.encode(r.password()),role);u.freeAccessUntil=Instant.now().plus(role==Role.CUSTOMER?Duration.ofDays(5):Duration.ofHours(2));return response(users.save(u));}
 @Transactional(readOnly=true) public AuthResponse login(LoginRequest r){User u=users.findByEmailIgnoreCase(r.email().trim()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect"));if(!encoder.matches(r.password(),u.passwordHash))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect");return response(u);}
 @Transactional(readOnly=true) public AccountResponse current(String email){return ApiModels.account(users.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found")));}
 @Transactional public AuthResponse updateProfile(String currentEmail,UpdateProfileRequest request){User u=users.findByEmailIgnoreCase(currentEmail).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found"));String nextEmail=request.email().trim().toLowerCase();if(!u.email.equalsIgnoreCase(nextEmail)&&users.existsByEmailIgnoreCase(nextEmail))throw new ResponseStatusException(HttpStatus.CONFLICT,"That email is already linked to another account");u.name=request.name().trim();u.email=nextEmail;return response(u);}
 private AuthResponse response(User u){return new AuthResponse(jwt.issue(u),"Bearer",jwt.ttlSeconds(),ApiModels.account(u));}
}
