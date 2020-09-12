package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    private final List<Item> itemList = Arrays.asList(
            new Item(null, "GoPro Hero 8", 499.99),
            new Item("id102", "DJI Mavek Pro", 1699.99),
            new Item(null, "Apple IPad 10", 299.99),
            new Item(null, "Nikon D750", 1800.99),
            new Item("id101", "Bose Headphones", 560.99)
    );

    @Before
    public void init() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void findAllTest() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void findByIdTest() {
        StepVerifier.create(itemReactiveRepository.findById("id101"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Test
    public void findItemByDescriptionTest() {
        StepVerifier.create(itemReactiveRepository.findItemByDescription("DJI Mavek Pro"))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(1699.99) && item.getId().equals("id102"))
                .verifyComplete();
    }

    @Test
    public void saveItemTest() {
        Item newItem = new Item(null, "Google Home Mini", 25.99);
        StepVerifier.create(itemReactiveRepository.save(newItem))
                .expectSubscription()
                .expectNextMatches(savedItem -> null != savedItem.getId() && savedItem.getDescription().equals(
                        "Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItemTest() {
        Double newPrice = 449.99;
        Mono<Item> updatedItem = itemReactiveRepository.findItemByDescription("GoPro Hero 8")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(itemReactiveRepository::save);

        StepVerifier.create(updatedItem.log("UPDATE: "))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(newPrice))
                .verifyComplete();
    }

    @Test
    public void deleteItemByIdTest() {
       itemReactiveRepository.findById("id101")
                .map(Item::getId)
                .flatMap(id -> itemReactiveRepository.deleteById(id))
                .block();

        StepVerifier.create(itemReactiveRepository.findById("id101"))
                .expectSubscription()
                .verifyComplete();
    }
}
