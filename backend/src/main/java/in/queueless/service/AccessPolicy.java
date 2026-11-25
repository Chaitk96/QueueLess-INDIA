package in.queueless.service;

import in.queueless.model.Domain.Role;
import in.queueless.model.Domain.User;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

final class AccessPolicy {
    private AccessPolicy() {}

    static Instant accessUntil(User user) {
        if(user.freeAccessUntil==null)return user.subscriptionUntil;
        if(user.subscriptionUntil==null)return user.freeAccessUntil;
        return user.freeAccessUntil.isAfter(user.subscriptionUntil)?user.freeAccessUntil:user.subscriptionUntil;
    }

    static void requireAccess(User user) {
        Instant until=accessUntil(user);
        if(until==null||until.isBefore(Instant.now())) {
            String message=user.role==Role.OWNER?"Your 2-hour free workspace access ended. Choose the annual plan to continue.":"Your free access ended. Choose a QueueLess pass to continue.";
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED,message);
        }
    }
}
