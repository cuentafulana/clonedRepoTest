package com.bext.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
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
        mono.subscribe(t -> log.info("t: {}", t));

        log.info("--------StepVerifier---------");

        StepVerifier.create(mono)
                .expectNext("MonoHasJustThis")
                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerErrorTest() {
        Mono<String> mono = Mono.just("MonoHasJustThis").log()
                .map(s -> {
                    throw new RuntimeException("test subscribe with error flow");
                });

        mono.subscribe(t -> log.info("t: {}", t), t -> log.error("error in the flow"));

        log.info("--------StepVerifier---------");

        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoSubscriberConsumerErrorConsumerCompleteConsumerTest() {
        Mono<String> mono = Mono.just("MonoHasJustThis")
                .log()
                .map(String::toUpperCase);

        mono.subscribe(t -> log.info("t: {}", t),
                Throwable::printStackTrace,
                () -> log.info("Complete!"));

        log.info("--------StepVerifier---------");

        StepVerifier.create(mono)
                .expectNext("MonoHasJustThis".toUpperCase())
                .verifyComplete();
    }

    @Test
    public void monoSubscriberConsumerErrorConsumerCompleteConsumerSubscriptionConsumerTest() {
        Mono<String> mono = Mono.just("MonoHasJustThis")
                .log()
                .map(String::toUpperCase);

        mono.subscribe(t -> log.info("t: {}", t),
                Throwable::printStackTrace,
                () -> log.info("Complete!"),
                subscription -> subscription.request(3));

        log.info("--------StepVerifier---------");

        StepVerifier.create(mono)
                .expectNext("MonoHasJustThis".toUpperCase())
                .verifyComplete();
    }

    @Test
    public void monoDoOnMethodsTest() {
        Mono<Object> mono = Mono.just("MonoHasJustThis")
                .log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("doOnSubscribe - {}", subscription))
                .doOnRequest(value -> log.info("doOnRequestThis - {}", value))
                .doOnNext(s -> log.info("doOnNext - {}", s))
                .flatMap(s -> Mono.empty())
                .doOnNext(s -> log.info("doOnNext - {}", s))
                .doOnSuccess(s -> log.info("doOnSuccess - {}", s));

        mono.subscribe(t -> log.info("t: {}", t),
                Throwable::printStackTrace,
                () -> log.info("Complete!"));
    }

    @Test
    public void monoDoOnErrorTest() {
        Mono<Object> monoError = Mono.error(new IllegalArgumentException("Mono Error"))
                .doOnError(throwable -> log.error("doOnError: {}", throwable.getMessage()))
                .log();

        log.info("--------StepVerifier---------");

        StepVerifier.create(monoError)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void monoDoOnErrorNopTest() {
        Mono<Object> monoError = Mono.error(new IllegalArgumentException("Mono Error"))
                .doOnError(throwable -> log.error("doOnError: {}", throwable.getMessage()))
                .doOnNext(o -> log.info("This doOnNext is not executed {}: ", o))
                .log();

        log.info("--------StepVerifier---------");

        StepVerifier.create(monoError)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void monoDoOnErrorResumeTest() {
        Mono<Object> monoError = Mono.error(new IllegalArgumentException("Mono Error"))
                .doOnError(throwable -> log.error("doOnError: {}", throwable.getMessage()))
                .onErrorResume(throwable -> {
                    log.info("in onErrorResume: {}", throwable.getMessage());
                    return Mono.just("Fixed the flow");
                })
                .log();

        log.info("--------StepVerifier---------");

        StepVerifier.create(monoError)
                .expectNext("Fixed the flow")
                .verifyComplete();
    }

    @Test
    public void monoDoOnErrorResumeDisableDoOnErrorTest() {
        Mono<Object> monoError = Mono.error(new IllegalArgumentException("Mono Error"))
                .onErrorResume(throwable -> {
                    log.info("in onErrorResume: {}", throwable.getMessage());
                    return Mono.just("Fixed the flow");
                })
                .doOnError(throwable -> log.error("doOnError: {}", throwable.getMessage()))
                .log();

        log.info("--------StepVerifier---------");

        StepVerifier.create(monoError)
                .expectNext("Fixed the flow")
                .verifyComplete();
    }

    @Test
    public void monoDoOnErrorReturnTest() {
        Mono<Object> monoError = Mono.error(new IllegalArgumentException("Mono Error"))
                .onErrorReturn("Returned by OnErrorReturn")
                .onErrorResume(throwable -> {
                    log.info("in onErrorResume: {}", throwable.getMessage());
                    return Mono.just("Fixed the flow");
                })
                .doOnError(throwable -> log.error("doOnError: {}", throwable.getMessage()))
                .log();

        log.info("--------StepVerifier---------");

        StepVerifier.create(monoError)
                .expectNext("Returned by OnErrorReturn")
                .verifyComplete();
    }

}
