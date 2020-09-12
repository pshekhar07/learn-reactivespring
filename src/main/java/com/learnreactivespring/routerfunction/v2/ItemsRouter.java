package com.learnreactivespring.routerfunction.v2;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.handlerfunction.v2.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemRouterFunction(final ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ItemConstants.ROUTER_FUNCTION_ALL_ITEMS_PATH).and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::getAllItems)
                .andRoute(GET(ItemConstants.ROUTER_FUNCTION_GET_ONE_ITEM_PATH).and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::getItemById)
                .andRoute(POST(ItemConstants.ROUTER_FUNCTION_ADD_ITEM_PATH).and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::addItem)
                .andRoute(DELETE(ItemConstants.ROUTER_FUNCTION_DELETE_ITEM_PATH).and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::deleteItem)
                .andRoute(PUT(ItemConstants.ROUTER_FUNCTION_UPDATE_ITEM_PATH).and(accept(MediaType.APPLICATION_JSON)),
                        itemsHandler::updateItem);
    }

}
