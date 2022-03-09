package com.bext.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class MonoTest {
    @Test
    public void monoSubscriberTest() {
        Mono<String> mono = Mono.just("MonoHasJustThis").log();
        mono.subscribe();

        log.info("Mono: {}", mono);
    }
}
