package p;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

@Slf4j
@org.springframework.stereotype.Controller
public class Controller {

    @PreAuthorize("hasAuthority('auth')")
    @MessageMapping("f&f")
    public Mono<Void> fireAndForget(final Data request, @AuthenticationPrincipal UserDetails user) {
        log.info("by '{}' with '{}'", user.getUsername(), user.getAuthorities());
        return Mono.empty();
    }
}
