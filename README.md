# Project Reactor Essentials Tests
A Walk through Project Reactor with test


- Dependencies
- setup Tests
- Mono.just, subscribe(), log()
- subscribe( Consumer, errorConsumer)
- subscribe( Consumer, errorConsumer, CompleteConsumer)
- subscribe( Consumer, errorConsumer, CompleteConsumer, subscriptionConsumer)
   Subscription::cancel
   Subscriptino::request(n)
- Mono doOnNext twice
- Mono doOnNext twice but emptied Mono between
- Mono doOnErrorTest, Mono.error
- Mono onErrorResume
- Mono onErrorResume disable doOnError onErrorReturn
- Flux subscriber
- Flux.range()
- flux.fromIterable( List.of(...) )
- flux.fromIterable( List.of(...), subscribe( consumer, errorConsumer, completedConsumer) Completed!
- flux.fromItetable( List.of(...), subscribe( consumer, errorConsumer, completedConsumer) request(n) NOT Completed!
- backpressure overriding subscription
- backpressure lambdas alternative BaseSubscriber
- Flux.interval simple do..XX.. activated when main Thread finish

        Flux<Long> fluxInterval = Flux.interval(Duration.ofMillis(100))
                .log()
                .doOnCancel(() -> log.info("doOnCancel"))                        //not called by main thread stopped
                .doOnError( err -> log.error("doOnError {}", err.getMessage()))  //not called by main thread stopped
                .doFinally(signalType -> log.info("doFinally {}", signalType))   //not called by main thread stopped
                .doOnTerminate(() -> log.info("doOnTerminate"))                  //not called by main thread stopped
                .doAfterTerminate(() -> log.info("doAfterTerminate"));           //not called by main thread stopped

        fluxInterval.subscribe(aLong -> log.info("interval: {}", aLong), Throwable::printStackTrace, () -> log.info("Completed!"));

        Thread.sleep(1000);  // to break the flux.interval

- Flux.interval simple do..XX.. activated when subscribe.dispose()

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

- Flux.interval disposed by subscriber programmatically (BaseSubscriber)
- Flux.interval terminated by flux.take(x), do..XX.. events triggered

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

- Flux.interval canceled by BaseSubscriber - cancel, do..XX.. events triggered
- interval withVirtualTime(Duration), thenAwait(Duration)
- .expectNoEvent(Duration)
- limitRate( int prefechRate)
- Hot Flux, ConnectableFlux<T> -- Flux.create()...publish()
- Hot Flux, ConnectableFlux<T> -- StepVerifier .then(publish::connect).thenConsumeWhile(i -> i < 5).expectNext(5,6...
- Hot Flux, ConnectableFlux<T> -- autoConnect( minSubscribers)
- OperatorsTest - subscribeOn [Schedulers.[single,boudendElastic]] - publishOn [Schedulers.[single, boundedElastic]]
- multiplePublishOnSchedulers, multipleSubscribeSchedulres, multiplePublishOnSubscribeOnSchedulers
- fromCallable (block IO)
- switchIfEmpty test previos to defer
- Mono.defer(() -> {...})
- Mono.defer atomicLong
- Flux.concat(...), fluxA,concatWith( fluxB), flux.combineLatest(...)
- Flux.merge( fluxA, fluxB), FluxA.mergeWith( fluxB)...
- Flux.mergeSequential( fluxA, fluxB), Flux.concat(WithErro), Flux.concatDelayError(1, fluxA, fluxB,...)
- Flux.map, Flux.flatMap
- Flux.flatMap delaying flux
- Flux.flatMapSequential
- Flux.zip( fluxA, fluxB,...) fluxA.zipWith( fluxB)
- BlockHound to test if code has blocking calls

                                                                                        // Class to Test
    @BeforeAll
    public  static void setup(){
        BlockHound.install();
    }

    @Test
    public void blockhoundinstallTest(){
        Assertions.assertThrows(RuntimeException.class, () -> {
        Mono.delay(Duration.ofSeconds(1))
                .doOnNext(it -> {
                    try {
                        Thread.sleep(10);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .block();
        });
    }                                                                                    // Class to Test
