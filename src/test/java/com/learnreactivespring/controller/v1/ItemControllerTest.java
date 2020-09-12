package com.learnreactivespring.controller.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "GoPro Hero 8", 499.99),
                new Item(null, "DJI Mavek Pro", 1699.99),
                new Item(null, "Apple IPad 10", 299.99),
                new Item(null, "Nikon D750", 1800.99),
                new Item("customid", "Bose Headphones", 560.99)
        );
    }

    @Before
    public void init() {
        itemReactiveRepository
                .deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void getItemsTest() {
        webTestClient
                .get()
                .uri(ItemConstants.PATH_ALL_ITEMS_V1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class);
    }

    @Test
    public void getItemByIdTest() {
        webTestClient
                .get()
                .uri(ItemConstants.PATH_ONE_ITEM_V1, "customid")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 560.99);
    }

    @Test
    public void getItemByIdTest_NotFound() {
        webTestClient
                .get()
                .uri(ItemConstants.PATH_ONE_ITEM_V1, "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void addItemTest() {
        webTestClient
                .post()
                .uri(ItemConstants.PATH_ADD_ITEM)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Item(null, "Apple MacBook Air", 799.99)), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description", "Apple MacBook Air");
    }

    @Test
    public void deleteItemTest() {
        webTestClient
                .delete()
                .uri(ItemConstants.PATH_DELETE_ITEM, "customid")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

        webTestClient
                .get()
                .uri(ItemConstants.PATH_ONE_ITEM_V1, "customid")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void updateItemTest() {
        webTestClient
                .put()
                .uri(ItemConstants.PATH_UPDATE_ITEM, "customid")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Item(null, "Bose In-ear Headphones", null)), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .consumeWith(item -> assertEquals(item.getResponseBody().getDescription(), "Bose In-ear " +
                        "Headphones"));

    }

    @Test
    public void updateItemTest_NotFound() {
        webTestClient
                .put()
                .uri(ItemConstants.PATH_UPDATE_ITEM, "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Item(null, "Bose In-ear Headphones", null)), Item.class)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    public void exceptionTest() {
        webTestClient
                .get()
                .uri(ItemConstants.PATH_ALL_ITEMS_V1 + "/runtimeexception")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Runtime Exception occured!");
    }
}
