package com.bext.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
                .expectNext(1, 2, 3, 4)
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
                .expectNext(1, 2, 3, 4)
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
                .expectNext(1, 2, 3, 4, 5, 6)
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
                .expectNext(1, 2, 3, 4, 5, 6)
                .verifyComplete();
    }

    @Test
    public void multiplePublishOnSchedulersTest() {
        Flux<Integer> fluxSubscribeOn = Flux.range(1, 8)
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("1st map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("2nd map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("3th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("4th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(fluxSubscribeOn)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8)
                .verifyComplete();
    }

    @Test
    public void multipleSubscribeOnSchedulersTest() {
        Flux<Integer> fluxSubscribeOn = Flux.range(1, 8)
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("1st map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("2nd map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("3th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("4th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(fluxSubscribeOn)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8)
                .verifyComplete();
    }

    @Test
    public void multiplePublishOnSubscribeOnSchedulersTest() {
        Flux<Integer> fluxSubscribeOn = Flux.range(1, 8)
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("1st map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("2nd map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("3th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("4th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(fluxSubscribeOn)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8)
                .verifyComplete();
    }

    @Test
    public void multiplePublishOnSubscribeOnSchedulers2Test() {
        Flux<Integer> fluxSubscribeOn = Flux.range(1, 8)
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("1st map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("2nd map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("3th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("4th map - {} - on thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        StepVerifier.create(fluxSubscribeOn)
                .expectSubscription()
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8)
                .verifyComplete();
    }

    @Test
    public void subscribeOnIOTest() {
        Mono<List<String>> listMono = Mono.fromCallable(() -> Files.readAllLines(Path.of("data.txt")))
                .log()
                .subscribeOn(Schedulers.boundedElastic());

        StepVerifier.create(listMono)
                .expectSubscription()
                .thenConsumeWhile(lines -> {
                    Assertions.assertFalse(lines.isEmpty());
                    log.info("lines size: {}", lines.size());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void switchIfEmptyTest() {
        Flux<Object> flux = fluxEmpty()
                .switchIfEmpty(Flux.just("It", "was", "empty", "but", "now", "not", "anymore"))
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("It", "was", "empty", "but", "now", "not", "anymore")
                .expectComplete()
                .verify();

    }

    private Flux<Object> fluxEmpty() {
        return Flux.empty();
    }

    @Test
    public void deferTest() throws InterruptedException {
        Mono<Long> monoTick = Mono.just(System.currentTimeMillis());
        Mono<Long> defer = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

        defer.subscribe(tick -> log.info("tick {}", tick));
        Thread.sleep(100);
        defer.subscribe(tick -> log.info("tick {}", tick));
        Thread.sleep(100);
        defer.subscribe(tick -> log.info("tick {}", tick));
        Thread.sleep(100);
        defer.subscribe(tick -> log.info("tick {}", tick));

        AtomicLong atomicLong = new AtomicLong();
        defer.subscribe(atomicLong::set);
        Assertions.assertTrue(atomicLong.get() > 0);
    }

    @Test
    public void fluxConcatTest() {
        Flux<String> fluxA = Flux.just("a","b");
        Flux<String> fluxB = Flux.just("c","d");

        Flux<String> fluxConcat = Flux.concat(fluxA, fluxB).log();

        StepVerifier.create(fluxConcat)
                .expectSubscription()
                .expectNext("a","b","c","d")
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxConcatWithTest() {
        Flux<String> fluxA = Flux.just("a","b");
        Flux<String> fluxB = Flux.just("c","d");

        Flux<String> fluxConcatWith = fluxA.concatWith(fluxB).log();

        StepVerifier.create(fluxConcatWith)
                .expectSubscription()
                .expectNext("a","b","c","d")
                .expectComplete()
                .verify();
    }

    @Test
    public void fluxCombineLatestTest() throws InterruptedException {

        Flux<String> fluxA = Flux.just("a","b","c","d").delayElements(Duration.ofMillis(100)).log();
        Flux<String> fluxB = Flux.just("1","2").delayElements(Duration.ofMillis(190)).log();

        Flux<String> fluxCombineLatest = Flux.combineLatest(fluxA, fluxB, (fa, fb) -> fa.toUpperCase() + fb);

        fluxCombineLatest.subscribe(System.out::println);

        StepVerifier.create(fluxCombineLatest)
                .expectSubscription()
                .expectNext("A1","B1","C1","C2","D2")
                .expectComplete()
                .verify();

        Thread.sleep(500);
    }
}
