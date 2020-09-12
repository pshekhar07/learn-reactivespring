package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFactoryTest {
    @Test
    public void fluxUsingIterable() {
        List<String> stringList = Arrays.asList("Adam", "Eve", "Romeo", "Juliet");

        StepVerifier.create(Flux.fromIterable(stringList).log())
                .expectNext("Adam", "Eve", "Romeo", "Juliet")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArrays() {
        String[] names = {"Adam", "Eve", "Romeo", "Juliet"};

        StepVerifier.create(Flux.fromArray(names).log())
                .expectNext("Adam", "Eve", "Romeo", "Juliet")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStreams() {
        List<String> names = Arrays.asList("Adam", "Eve", "Romeo", "Juliet");

        StepVerifier.create(Flux.fromStream(names.stream()).log())
                .expectNext("Adam", "Eve", "Romeo", "Juliet")
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        StepVerifier.create(Mono.fromSupplier(() -> "Ruzek").log())
                .expectNext("Ruzek")
                .verifyComplete();
    }

    @Test
    public void monoEmptyorNullTest() {
        StepVerifier.create(Mono.justOrEmpty(null).log())
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        StepVerifier.create(Flux.range(1, 5).log())
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}
