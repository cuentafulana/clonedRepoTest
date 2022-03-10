package com.bext.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {
    @Test
    public void monoSubscriberTest() {
        Mono<String> mono = Mono.just("MonoHasJustThis").log();
        mono.subscribe();

        log.info("--------StepVerifier---------");

        StepVerifier.create(mono)
                .expectNext("MonoHasJustThis")
                .verifyComplete();

    }

    @Test
    public void monoSubscriberConsumerTest() {
        Mono<String> mono = Mono.just("MonoHasJustThis").log();
        mono.subscribe( t -> log.info("t: {}", t));

        log.info("--------StepVerifier---------");

        StepVerifier.create(mono)
                .expectNext("MonoHasJustThis")
                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerErrorTest() {
        Mono<String> mono = Mono.just("MonoHasJustThis").log()
                .map( s -> {throw new RuntimeException("test subscribe with error flow");});

        mono.subscribe( t -> log.info("t: {}", t), t -> log.error("error in the flow"));

        log.info("--------StepVerifier---------");

        StepVerifier.create(mono)
                .expectError( RuntimeException.class)
                .verify();
    }

    @Test
    public void monoSubscriberConsumerErrorConsumerCompleteConsumerTest() {
        Mono<String> mono = Mono.just("MonoHasJustThis")
                .log()
                .map(String::toUpperCase);

        mono.subscribe( t -> log.info("t: {}", t),
                Throwable::printStackTrace,
                () -> log.info("Complete!"));

        log.info("--------StepVerifier---------");

        StepVerifier.create(mono)
                .expectNext("MonoHasJustThis".toUpperCase())
                .verifyComplete();
    }

}
