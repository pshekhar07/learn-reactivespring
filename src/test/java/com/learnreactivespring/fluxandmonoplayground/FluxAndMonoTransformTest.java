package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {
    private final List<String> names = Arrays.asList("adam", "eve", "romeo", "juliet");

    @Test
    public void fluxTransformTestWithMap() {
        StepVerifier.create(Flux.fromIterable(names).map(String::toUpperCase).log())
                .expectNext("ADAM", "EVE", "ROMEO", "JULIET")
                .verifyComplete();
    }

    @Test
    public void fluxTransformTestWithFlatMap() {
        Flux<String> fluxString = Flux.just("adam", "eve", "romeo", "juliet")
                .flatMap(name -> {
                    return Flux.fromIterable(this.convertToList(name));
                }).log();
        
        StepVerifier.create(fluxString)
                .expectNext("adam", "Hi", "eve", "Hi", "romeo", "Hi", "juliet", "Hi")
                .verifyComplete();
    }

    @Test
    public void fluxTransformTestWithFlatMap_Window_Using_parallel() {
        Flux<String> fluxString = Flux.just("adam", "eve", "romeo", "juliet")
                .window(2) // Flux<Flux<String>>
                .flatMap(nameFlux -> {
                    return nameFlux.map(this::convertToList)
                            .flatMap(Flux::fromIterable).subscribeOn(Schedulers.parallel());
                }).log();

        StepVerifier.create(fluxString)
                .expectNextCount(8)
                .verifyComplete();
    }

    //https://javatechnicalwealth.com/blog/reactive-flatmap/
    @Test
    public void fluxTransformTestWithFlatMap_Window_Using_parallel_inorder() {
        Flux<String> fluxString = Flux.just("adam", "eve", "romeo", "juliet")
                .window(2) // Flux<Flux<String>>
                .flatMapSequential(nameFlux -> {
                    return nameFlux.map(this::convertToList)
                            .flatMap(Flux::fromIterable).subscribeOn(Schedulers.parallel());
                }).log();

        StepVerifier.create(fluxString)
                .expectNextCount(8)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "Hi");
    }

    @Test
    public void fluxTransformTestWithFlatMapRepeat() {
        StepVerifier.create(Flux.fromIterable(names).flatMap(name -> Flux.just(name.toUpperCase())).repeat(1).log())
                .expectNext("ADAM", "EVE", "ROMEO", "JULIET", "ADAM", "EVE", "ROMEO", "JULIET")
                .verifyComplete();
    }
}
