package com.learnreactivespring.routerfunction;

import com.learnreactivespring.handlerfunction.FluxAndMonoHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(FluxAndMonoHandlerFunction fluxAndMonoHandlerFunction) {
        return RouterFunctions
                .route()
                .GET("/functional/flux",accept(MediaType.APPLICATION_JSON), fluxAndMonoHandlerFunction::flux)
                .GET("/functional/mono", accept(MediaType.APPLICATION_JSON), fluxAndMonoHandlerFunction::mono)
                .build();
    }

}
