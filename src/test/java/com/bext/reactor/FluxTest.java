package com.bext.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class FluxTest {

    @Test
    public void fluxSubsccriberTest(){
        Flux<String> fluxString = Flux.just("Flow","from","flux",".just()")
                .log();
        fluxString.subscribe();

        log.info("--------StepVerifier---------");

        StepVerifier.create(fluxString)
                .expectNext("Flow","from","flux",".just()")
                .verifyComplete();
    }

    @Test
    public void fluxSubscribeNumbersTest(){
        Flux<Integer> fluxInteger = Flux.range(1, 5)
                .log();
        fluxInteger.subscribe(integer -> log.info("flux integer {}", integer));

        log.info("--------StepVerifier---------");

        StepVerifier.create(fluxInteger)
                .expectNext(1, 2,3 ,4, 5)
                .verifyComplete();
    }
}
