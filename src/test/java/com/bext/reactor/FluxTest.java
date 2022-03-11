package com.bext.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
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
        Flux.concat();
        fluxInteger.subscribe(integer -> log.info("flux integer {}", integer), Throwable::printStackTrace
                , () -> log.info("Completed!"));

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
                .expectNext(1, 2, 3)
                .expectError(IndexOutOfBoundsException.class)
                .verify();
    }

    @Test
    public void fluxSubscriberNumberBackPressureTest() {
        Flux<Integer> fluxInteger = Flux.range(1, 10)
                .log();

        fluxInteger.subscribe(new Subscriber<Integer>() {
            private Subscription subscription;
            private int count = 0;
            private final int requestCount = 3;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                s.request(requestCount);
            }

            @Override
            public void onNext(Integer integer) {
                count++;
                if (count % requestCount == 0) {
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });

        log.info("--------StepVerifier---------");

        StepVerifier.create(fluxInteger)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    public void fluxSubscriberNumberBackPressureBaseSubscribeTest() {
        Flux<Integer> fluxInteger = Flux.range(1, 10)
                .log();

        fluxInteger.subscribe(new BaseSubscriber<>() {
            private int count = 0;
            private final int requestCount = 3;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(3);
            }

            @Override
            protected void hookOnNext(Integer value) {
                count++;
                if (count % requestCount == 0) {
                    request(requestCount);
                }
            }
        });

        log.info("--------StepVerifier---------");

        StepVerifier.create(fluxInteger)
                .expectSubscription()
                .expectNext(1,2,3,4,5,6,7,8,9,10)
                .verifyComplete();
    }

    @Test
    public void intervalTest() throws InterruptedException {
        Flux<Long> fluxInterval = Flux.interval(Duration.ofMillis(100))
                .log()
                .doOnCancel(() -> log.info("doOnCancel"))                        //not called by main thread stopped
                .doOnError( err -> log.error("doOnError {}", err.getMessage()))  //not called by main thread stopped
                .doFinally(signalType -> log.info("doFinally {}", signalType))   //not called by main thread stopped
                .doOnTerminate(() -> log.info("doOnTerminate"))                  //not called by main thread stopped
                .doAfterTerminate(() -> log.info("doAfterTerminate"));           //not called by main thread stopped

        fluxInterval.subscribe(aLong -> log.info("interval: {}", aLong), Throwable::printStackTrace, () -> log.info("Completed!"));

        Thread.sleep(1000);  // to break the flux.interval
    }

    @Test
    public void intervalDisposedTest() throws InterruptedException {
        Flux<Long> fluxInterval = Flux.interval(Duration.ofMillis(100))
                .log()
                .doOnCancel(() -> log.info("doOnCancel"))                        //called when subscribe.dispose
                .doOnError( err -> log.error("doOnError {}", err.getMessage()))  //not called by main thread stopped or subscription disposed
                .doFinally(signalType -> log.info("doFinally {}", signalType))   //called when subscribe.dispose
                .doOnTerminate(() -> log.info("doOnTerminate"))                  //not called by main thread stopped or subscription disposed
                .doAfterTerminate(() -> log.info("doAfterTerminate"));           //not called by main thread stopped or subscription disposed

        Disposable subscribe = fluxInterval.subscribe(aLong -> log.info("interval: {}", aLong), Throwable::printStackTrace, () -> log.info("Completed!"));
        Thread.sleep(300);
        subscribe.dispose();

        Thread.sleep(1000);  // to break the flux.interval
    }

    @Test
    public void intervalDisposedBySubscriberProgrammaticallyTest() throws InterruptedException {
        Flux<Long> fluxInterval = Flux.interval(Duration.ofMillis(100))
                .log()
                .doOnCancel(() -> log.info("doOnCancel"))                        //called when subscribe.dispose
                .doOnError(err -> log.error("doOnError {}", err.getMessage()))  //not called by main thread stopped or subscription disposed
                .doFinally(signalType -> log.info("doFinally {}", signalType))   //called when subscribe.dispose
                .doOnTerminate(() -> log.info("doOnTerminate"))                  //not called by main thread stopped or subscription disposed
                .doAfterTerminate(() -> log.info("doAfterTerminate"));           //not called by main thread stopped or subscription disposed

        BaseSubscriber subscriber = new BaseSubscriber() {
            @Override
            public void dispose() {
                super.dispose();
            }

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
                log.info("hookOnSubscribe");
            }

            @Override
            protected void hookOnNext(Object value) {
                request(1);
                if (value.toString().equals("4") ) dispose();
            }
        };

        fluxInterval.subscribe( subscriber);

        Thread.sleep(1000);  // to break the flux.interval
    }

    @Test
    public void intervalTerminatedTest() throws InterruptedException {
        Flux<Long> fluxInterval = Flux.interval(Duration.ofMillis(100))
                .log()
                .take(3)
                .doOnCancel(() -> log.info("doOnCancel"))                        //Not called
                .doOnError(err -> log.error("doOnError {}", err.getMessage()))   //Not called
                .doOnTerminate(() -> log.info("doOnTerminate"))                           //called 1st when flux finish
                .doAfterTerminate(() -> log.info("doAfterTerminate"))                     //called 2nd when flux finish
                .doFinally(signalType -> log.info("doFinally signal: {}", signalType));   //called 3rd when flux finish

        fluxInterval.subscribe(aLong -> log.info("interval: {}", aLong), Throwable::printStackTrace, () -> log.info("Completed!"));

        log.info("--------StepVerifier---------");

        StepVerifier.create(fluxInterval)
                .expectSubscription()
                .expectNext(0L,1L,2L)
                .expectComplete()
                .verify();

        Thread.sleep(1000);  // to break the flux.interval
    }

    @Test
    public void intervalSubscribeCancelProgrammaticallyBaseSubscriberTest() throws InterruptedException {
        Flux<Long> fluxInterval = Flux.interval(Duration.ofMillis(100))
                .log()
                .doOnCancel(() -> log.info("doOnCancel"))                        //called when subscribe.cancel
                .doOnError(err -> log.error("doOnError {}", err.getMessage()))   //not called by main thread stopped or subscription canceled
                .doOnTerminate(() -> log.info("doOnTerminate"))                  //not called by main thread stopped or subscription canceled
                .doAfterTerminate(() -> log.info("doAfterTerminate"))            //not called by main thread stopped or subscription canceled
                .doFinally(signalType -> log.info("doFinally {}", signalType));  //called when subscribe.cancel

        BaseSubscriber subscriber = new BaseSubscriber() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
                log.info("hookOnSubscribe");
            }

            @Override
            protected void hookOnNext(Object value) {
                request(1);
                if (value.toString().equals("4") ) cancel();
            }
        };

        fluxInterval.subscribe( subscriber);

        Thread.sleep(1000);  // to break the flux.interval
    }

    @Test
    public void intervalWithVirtualTest(){
        StepVerifier.withVirtualTime(this::fluxIntervalDuration)
                .expectSubscription()
                .thenAwait(Duration.ofDays(1))
                .expectNext(0L)
                .thenAwait(Duration.ofDays(1))
                .expectNext(1L)
                .thenCancel()
                .verify();
    }

    private Flux<Long> fluxIntervalDuration() {
        return Flux.interval(Duration.ofDays(1))
                .take(10)
                .log();
    }
}