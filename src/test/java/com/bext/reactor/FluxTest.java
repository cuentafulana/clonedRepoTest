package com.bext.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

@Slf4j
public class FluxTest {

    @Test
    public void fluxSubsccriberTest() {
        Flux<String> fluxString = Flux.just("Flow", "from", "flux", ".just()")
                .log();
        fluxString.subscribe();

        log.info("--------StepVerifier---------");

        StepVerifier.create(fluxString)
                .expectNext("Flow", "from", "flux", ".just()")
                .verifyComplete();
    }

    @Test
    public void fluxSubscribeNumbersTest() {
        Flux<Integer> fluxInteger = Flux.range(1, 5)
                .log();
        fluxInteger.subscribe(integer -> log.info("flux integer {}", integer));

        log.info("--------StepVerifier---------");

        StepVerifier.create(fluxInteger)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void fluxSubscribeFromIterableTest() {
        Flux<Integer> fluxInteger = Flux.fromIterable(List.of(1, 2, 3, 4, 5))
                .log();

        fluxInteger.subscribe(integer -> log.info("flux integer {}", integer), Throwable::printStackTrace
        ,() -> log.info("Completed!"), subscription -> subscription.request(3));

        log.info("--------StepVerifier---------");

        StepVerifier.create(fluxInteger)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumberErrorTest() {
        Flux<Integer> fluxInteger = Flux.range(1, 5)
                .log()
                .map(i -> {
                    if (i == 4) {
                        throw new IndexOutOfBoundsException(" index == 4, Error");
                    }
                    return i;
                });

        fluxInteger.subscribe(integer -> log.info("flux integer {}", integer), Throwable::printStackTrace,
                () -> log.info("Complete!"));

        log.info("--------StepVerifier---------");

        StepVerifier.create(fluxInteger)
                .expectNext(1,2,3)
                .expectError(IndexOutOfBoundsException.class)
                .verify();

    }
}
