package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void fluxMergeTest() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        StepVerifier.create(Flux.merge(flux1, flux2).log())
        .expectNext("A", "B", "C", "D", "E", "F")
        .verifyComplete();
    }

    @Test
    public void fluxMergeTest_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        StepVerifier.create(Flux.merge(flux1, flux2).log())
                //.expectNext("A", "B", "C", "D", "E", "F") //ordered is not maintained
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void fluxConcatTest() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        StepVerifier.create(Flux.concat(flux1, flux2).log())
                //.expectNext("A", "B", "C", "D", "E", "F") //ordered is not maintained
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void fluxConcatTest_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        StepVerifier.create(Flux.concat(flux1, flux2).log())
                .expectNext("A", "B", "C", "D", "E", "F") //ordered is maintained, but concat waits before previous
                // publisher's onComplete has been invoked, from the source chain, in sequence.
                //.expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void fluxMergeTest_withOrder() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        StepVerifier.create(Flux.mergeOrdered(flux1, flux2).log())
                .expectNext("A", "B", "C", "D", "E", "F") //ordered is maintained, but concat waits before previous
                // publisher's onComplete has been invoked, from the source chain, in sequence.
                //.expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void fluxZipTest() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        StepVerifier.create(Flux.zip(flux1, flux2, (f1, f2) -> f1.concat(f2.toLowerCase())).log())
                .expectNext("Ad", "Be", "Cf")
                .verifyComplete();
    }

    @Test
    public void fluxZipTest_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C", "D").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        StepVerifier.create(Flux.zip(flux1, flux2, (f1, f2) -> f1.concat(f2.toLowerCase())).log())
                .expectNext("Ad", "Be", "Cf") //eager subscription to both flux's elements, waits to finish and
                // then
                // moves to next pair. Also, if either of elements are null, drop.
                .verifyComplete();
    }
}
