package com.bext.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@Slf4j
public class OperatorsTest {

    @Test
    public void subscribeOnSchedulersSingleTest() {
        Flux<Integer> fluxSubscribeOn = Flux.range(1, 4)
                .map(i -> {
                    log.info("1st map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("2nd map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(fluxSubscribeOn)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    public void subscribeOnSchedulersBoundedElasticTest() {
        Flux<Integer> fluxSubscribeOn = Flux.range(1, 4)
                .map(i -> {
                    log.info("1st map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("2nd map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(fluxSubscribeOn)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    public void publishOnSchedulersSingleTest() {
        Flux<Integer> fluxSubscribeOn = Flux.range(1, 6)
                .map(i -> {
                    log.info("1st map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("2nd map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("3th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(fluxSubscribeOn)
                .expectSubscription()
                .expectNext(1,2,3,4,5,6)
                .verifyComplete();
    }

    @Test
    public void publishOnSchedulersBoundedElasticTest() {
        Flux<Integer> fluxSubscribeOn = Flux.range(1, 6)
                .map(i -> {
                    log.info("1st map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("2nd map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("3th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(fluxSubscribeOn)
                .expectSubscription()
                .expectNext(1,2,3,4, 5,6)
                .verifyComplete();
    }
}
