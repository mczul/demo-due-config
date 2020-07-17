package de.mczul.configuration.web;

import de.mczul.configuration.common.ScheduledConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ReactiveRouterConfiguration {
    private final ScheduledConfigHandler scheduledConfigHandler;

    @Bean
    RouterFunction<ServerResponse> routerFunction(ScheduledConfigHandler scheduledConfigHandler) {
        return route()
                .GET("/api/", scheduledConfigHandler::handleGet)
                .GET("/api/{" + RestConstants.PATH_VAR_KEY + "}", scheduledConfigHandler::handleGetKey)
                .build();
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    public static class ScheduledConfigHandler {
        final ScheduledConfigService scheduledConfigService;

        public Mono<ServerResponse> handleGet(ServerRequest serverRequest) {
            serverRequest.queryParam(RestConstants.QUERY_PARAM_PAGE_INDEX);
            serverRequest.queryParam(RestConstants.QUERY_PARAM_PAGE_SIZE);
            serverRequest.pathVariable(RestConstants.PATH_VAR_KEY);
            return Mono.empty();
        }

        public Mono<ServerResponse> handleGetKey(ServerRequest serverRequest) {
            serverRequest.queryParam(RestConstants.QUERY_PARAM_PAGE_INDEX);
            serverRequest.queryParam(RestConstants.QUERY_PARAM_PAGE_SIZE);
            serverRequest.pathVariable(RestConstants.PATH_VAR_KEY);
            return Mono.empty();
        }

    }

}
