package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import static com.learnreactivespring.constants.ItemConstants.*;

@RestController
@Slf4j
public class ItemController {
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(PATH_ALL_ITEMS_V1)
    public Flux<Item> getItems() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(PATH_ALL_ITEMS_V1 + "/runtimeexception")
    public Flux<Item> getItemsWithExcpetion() {
        return itemReactiveRepository.findAll().concatWith(Mono.error(new RuntimeException("Runtime Exception " +
                "occured!"))).log("Emit: ");
    }

    @GetMapping(PATH_ONE_ITEM_V1)
    public Mono<ResponseEntity<Item>> getItemById(@PathVariable String id) {
        return itemReactiveRepository
                .findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(PATH_ADD_ITEM)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> addItem(@RequestBody Item item) {
        return itemReactiveRepository
                .save(item);
    }

    @DeleteMapping(PATH_DELETE_ITEM)
    public Mono<Void> deleteItem(@PathVariable String id) {
        return itemReactiveRepository
                .deleteById(id);
    }

    @PutMapping(PATH_UPDATE_ITEM)
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item updatedItem) {
        return itemReactiveRepository
                .findById(id)
                .flatMap(item -> {
                    item.setPrice(updatedItem.getPrice());
                    item.setDescription(updatedItem.getDescription());
                    return itemReactiveRepository.save(item);
                })
                .map(savedItem -> new ResponseEntity<>(savedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @GetMapping("/test")
    public Mono<String> test() {
        Scheduler a = Schedulers.newParallel("SCA", 4);
        Scheduler b = Schedulers.newParallel("SCB", 4);
        Scheduler c = Schedulers.newParallel("SCC", 4);
        Mono.just("Hello")
                //.publishOn(a)
                .map(text -> {System.out.println("Hello from map thread : " + Thread.currentThread().getName()); return text + " World";})
                //.publishOn(b)
                .doOnNext(text -> System.out.println("Hello from onSuccess thread : " + Thread.currentThread().getName()))
                .doOnSuccess(text -> System.out.println("Hello from onError thread : " + Thread.currentThread().getName()))
                .subscribeOn(c)
                .subscribe();
        return Mono.just("Done");
    }
}
