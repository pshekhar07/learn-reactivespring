package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {
    @Test
    public void fluxTest() {
        Flux<String> fluxString = Flux.just("Spring", "SpringBoot", "Reactive Spring").log()
                .concatWith(Flux.just("Hello World")).concatWith(Flux.error(new RuntimeException("Exception Occurred" +
                        ".")));
        fluxString.subscribe(System.out::println, System.out::println);
    }

    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> fluxString = Flux.just("Spring", "SpringBoot", "Reactive Spring").log();

        StepVerifier.create(fluxString)
                .expectNext("Spring")
                .expectNext("SpringBoot")
                .expectNext("Reactive Spring")
                .verifyComplete();
    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> fluxString =
                Flux.just("Spring", "SpringBoot", "Reactive Spring").concatWith(Flux.error(new RuntimeException(
                        "Exception Occurred."))).log();

        StepVerifier.create(fluxString)
                .expectNext("Spring")
                .expectNext("SpringBoot")
                .expectNext("Reactive Spring")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoTest() {
        StepVerifier.create(Mono.just("Spring").log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_WithError() {
        StepVerifier.create(Mono.error(new RuntimeException("Error Occurred.")).log())
                .expectError(RuntimeException.class)
                .verify();
    }

}
