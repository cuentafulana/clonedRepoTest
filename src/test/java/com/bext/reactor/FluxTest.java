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
}
