package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Autowired
    private ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    private ReactiveMongoOperations reactiveMongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
        createCappedCollection();
        setupDataForCappedCollection();
    }

    private void setupDataForCappedCollection() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Integer " + i, 100.0 + i));
        itemReactiveCappedRepository.insert(itemCappedFlux)
                .subscribe(savedcappedItem -> log.info("Inserted capped item: {}", savedcappedItem));
    }

    private void createCappedCollection() {
        reactiveMongoOperations.dropCollection(ItemCapped.class)
                .then(reactiveMongoOperations.createCollection(ItemCapped.class,
                        CollectionOptions.empty().capped().maxDocuments(20).size(50000))).subscribe();

    }

    private void initializeData() {
        itemReactiveRepository
                .deleteAll()
                .thenMany(Flux.fromIterable(getItemIterable()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(savedItem -> log.info("Saved item - [{}] in MongoDB.", savedItem))
                .subscribe();

    }

    private List<Item> getItemIterable() {
        return Arrays.asList(
                new Item(null, "GoPro Hero 8", 469.99),
                new Item(null, "DJI Mavek Pro", 1699.99),
                new Item(null, "Apple IPad 10", 299.99),
                new Item(null, "Nikon D750", 1800.99),
                new Item("customid", "Bose Headphones", 560.99)
        );
    }
}
