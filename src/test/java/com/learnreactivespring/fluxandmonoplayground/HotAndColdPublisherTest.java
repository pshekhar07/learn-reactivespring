package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class HotAndColdPublisherTest {

    @Test
    public void coldPublisher() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1)).log();

        stringFlux.subscribe(e -> System.out.println("Subscriber 1 : " + e));

        Thread.sleep(3000);

        stringFlux.subscribe(e -> System.out.println("Subscriber 2 : " + e));
        Thread.sleep(4000);
    }

    @Test
    public void backPressurePublisher() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1)).log();

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        connectableFlux.subscribe(new BaseSubscriber<String>() {
            @Override
            protected void hookOnNext(String value) {
                super.hookOnNext(value);
                request(1);
                System.out.println("Subscriber 1 : " + value);
            }
        });
        Thread.sleep(3000);
    }

    @Test
    public void hotPublisher() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1)).log();

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        //connectableFlux.subscribe(e -> System.out.println("Subscriber 1 : " + e));
        Thread.sleep(3000);

        //connectableFlux.subscribe(e -> System.out.println("Subscriber 2 : " + e));
        Thread.sleep(4000);
    }
}
