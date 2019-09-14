package com.ww.company.api;

import com.ww.company.api.handler.Handler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.HashSet;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

@Configuration
public class Router implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * Enables routes
     *
     * @return functional routes
     */
    @Bean
    public RouterFunction<ServerResponse> routes() {
        Flux<Object> handlers =
                Flux.fromStream(context.getBeansWithAnnotation(Handler.class).values().stream())
                        .publish()
                        .autoConnect(2);

        return Mono.zip(
                handlers
                        .map(handler -> handler.getClass().getAnnotation(Handler.class).path())
                        .scanWith(HashSet::new, (set, element) -> {
                            if (!set.add(element)) {
                                throw new IllegalArgumentException("Duplicate path was detected : " + element);
                            }
                            return set;
                        })
                        .last(),
                handlers
                        .map(this::extractRouter)
                        .reduce(RouterFunction::and),
                (array, router) -> router
        )
                .block();
    }

    @SuppressWarnings("unchecked")
    private RouterFunction<ServerResponse> extractRouter(Object handler) {
        Handler annotation = handler.getClass().getAnnotation(Handler.class);
        String path = annotation.path();
        String router = annotation.routerMethod();
        Method methodToInvoke = requireNonNull(findMethod(handler.getClass(), router), "Router method has not been found for handler " + path);
        return nest(path(path), (RouterFunction<ServerResponse>) requireNonNull(invokeMethod(methodToInvoke, handler), "Handler method can't be called"));
    }

}
