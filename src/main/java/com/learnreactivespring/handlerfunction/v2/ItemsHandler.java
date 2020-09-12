package com.learnreactivespring.handlerfunction.v2;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class ItemsHandler {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findById(serverRequest.pathVariable("id")), Item.class);
    }

    public Mono<ServerResponse> addItem(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(Item.class)
                .flatMap(item -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item).log("saving"), Item.class).log("serverresponse")).log("serverrequest");
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .body(itemReactiveRepository.deleteById(serverRequest.pathVariable("id")), Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        return itemReactiveRepository
                .findById(serverRequest.pathVariable("id"))
                .flatMap(item -> serverRequest
                        .bodyToMono(Item.class)
                        .map(updatedItem -> {
                            Optional.ofNullable(updatedItem.getDescription()).ifPresent(item::setDescription);
                            Optional.ofNullable(updatedItem.getPrice()).ifPresent(item::setPrice);
                            return item;
                        })
                        .flatMap(savedItem -> ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(itemReactiveRepository.save(savedItem), Item.class)
                        )
                );
    }
}
