package com.ww.company.api.handler;

import com.ww.company.model.Company;
import com.ww.company.repository.MongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

import static org.springframework.web.reactive.function.server.ServerResponse.created;

@Handler(path = "/nosql/companies")
@RequiredArgsConstructor
public class NoSQLCompanies {

    private final MongoRepository mongoRepository;

    public RouterFunction<ServerResponse> router() {
        return RouterFunctions.route()
                .POST("/", handlerFunction())
                .build();
    }

    private HandlerFunction<ServerResponse> handlerFunction() {
        return request -> request
                .bodyToMono(Company.class)
                .flatMap(mongoRepository::save)
                .flatMap(company ->
                        created( URI.create("/nosql/companies/" + company.getId()) ).build()
                )
                .subscribeOn(Schedulers.elastic());
    }

}
