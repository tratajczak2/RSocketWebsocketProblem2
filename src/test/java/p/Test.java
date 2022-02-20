package p;

import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

@Slf4j
@SpringBootTest
public class Test {

    private static RSocketRequester requester;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder) {
        requester = builder
                .setupMetadata(new UsernamePasswordMetadata("user", "pass"),
                        MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString()))
                .rsocketStrategies(b ->
                        b.encoder(new SimpleAuthenticationEncoder()))
                .websocket(URI.create("ws://localhost:6500"));

        Hooks.onErrorDropped(error -> {
            log.info("Exception happened:" + error.getMessage());
        });
    }

    @org.junit.jupiter.api.Test
    public void testFireAndForget() {
        Mono<Void> result = requester
                .route("f&f")
                .data(new Data("d"))
                .retrieveMono(Void.class);

        StepVerifier
                .create(result)
                .verifyComplete();
    }
}
