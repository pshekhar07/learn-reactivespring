package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {
    private final List<String> names = Arrays.asList("adam", "eve", "romeo", "juliet");

    @Test
    public void fluxFilterTest() {
        StepVerifier.create(Flux.fromIterable(names).filter(name -> name.startsWith("a")).log())
        .expectNext("adam")
        .verifyComplete();
    }
}
