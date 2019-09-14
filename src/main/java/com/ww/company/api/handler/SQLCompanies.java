package com.ww.company.api.handler;

import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Handler(path = "/sql/companies")
public class SQLCompanies {

    public RouterFunction<ServerResponse> router() {
        return RouterFunctions.route()
                .GET("/", handlerFunction())
                .build();
    }

    private HandlerFunction<ServerResponse> handlerFunction() {
        return request -> ServerResponse.ok().build();
    }

}
