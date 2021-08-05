package gray.light.gateway.route;

import gray.light.gateway.model.ServiceMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 通过数据源加载
 *
 * @author XyParaCrim
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSourceDefinitionLocator implements RouteDefinitionLocator {

    private static final String SERVICE_METADATA_KEY = "service-metadata";

    private final ReactiveRedisTemplate<String, ServiceMetadata> serviceMetadataTemplate;

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return serviceMetadataTemplate
                .opsForHash()
                .entries(SERVICE_METADATA_KEY)
                .flatMap(entries -> {
                    LOG.info("{}: {}", entries.getKey(), entries.getValue());
                    return Flux.empty();
                });
    }

}
