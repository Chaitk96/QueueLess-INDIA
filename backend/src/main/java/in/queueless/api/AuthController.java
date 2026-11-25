package in.queueless.api;
import in.queueless.api.ApiModels.*;
import in.queueless.model.Domain.Role;
import in.queueless.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
 private final AuthService auth;public AuthController(AuthService auth){this.auth=auth;}
 @PostMapping("/register") public AuthResponse register(@Valid @RequestBody RegisterRequest r){return auth.register(r,Role.CUSTOMER);}
 @PostMapping("/register-owner") public AuthResponse registerOwner(@Valid @RequestBody RegisterRequest r){return auth.register(r,Role.OWNER);}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest r){return auth.login(r);}
 @GetMapping("/me") public AccountResponse me(Authentication a){return auth.current(a.getName());}
 @PutMapping("/me") public AuthResponse update(@Valid @RequestBody UpdateProfileRequest r,Authentication a){return auth.updateProfile(a.getName(),r);}
}
