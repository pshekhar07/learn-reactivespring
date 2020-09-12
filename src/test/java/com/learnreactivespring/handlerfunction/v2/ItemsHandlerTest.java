package com.learnreactivespring.handlerfunction.v2;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemsHandlerTest {
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
    public void getAllItemsTest() {
        webTestClient
                .get()
                .uri(ItemConstants.ROUTER_FUNCTION_ALL_ITEMS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .consumeWith(listEntityExchangeResult -> {
                            listEntityExchangeResult.getResponseBody().forEach(item -> {
                                Assert.assertTrue(data()
                                        .stream()
                                        .anyMatch(dataItem ->
                                                dataItem.getDescription().equals(item.getDescription())
                                                        && dataItem.getPrice().equals(item.getPrice())
                                        ));
                            });
                        }
                );
    }

    @Test
    public void getItemByIdTest() {
        webTestClient
                .get()
                .uri(ItemConstants.ROUTER_FUNCTION_GET_ONE_ITEM_PATH, "customid")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .jsonPath("$.description").isEqualTo("Bose Headphones")
                .jsonPath("$.price").isEqualTo(560.99);
    }

    @Test
    public void addItemTest() {
        webTestClient
                .post()
                .uri(ItemConstants.ROUTER_FUNCTION_ADD_ITEM_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Item(null, "SamSung TV", 345.67)), Item.class)
                .exchange()
                .expectBody()
                .jsonPath("$.description").isEqualTo("SamSung TV")
                .jsonPath("$.price").isEqualTo(345.67)
                .jsonPath("$.id").isNotEmpty();
    }

    @Test
    public void deleteItemTest() {
        webTestClient
                .delete()
                .uri(ItemConstants.ROUTER_FUNCTION_DELETE_ITEM_PATH, "customid")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(Void.class);
    }

    @Test
    public void updateItemTest() {
        webTestClient
                .put()
                .uri(ItemConstants.ROUTER_FUNCTION_UPDATE_ITEM_PATH, "customid")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Item(null, null, 599.99)), Item.class)
                .exchange()
                .expectBody()
                .jsonPath("$.description").isEqualTo("Bose Headphones")
                .jsonPath("$.price").isEqualTo(599.99)
                .jsonPath("$.id").isNotEmpty();
    }
}
