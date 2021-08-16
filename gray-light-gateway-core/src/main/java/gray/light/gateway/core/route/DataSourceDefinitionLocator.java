package gray.light.gateway.core.route;

import gray.light.gateway.core.properties.ServiceLocatorProperties;
import gray.light.gateway.definition.ServiceMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * 通过数据源加载
 *
 * @author XyParaCrim
 */
@Slf4j
@RequiredArgsConstructor
public class DataSourceDefinitionLocator implements RouteDefinitionLocator {

    private static final String SERVICE_METADATA_KEY = "service-metadata";

    private final ServiceLocatorProperties properties;

    private final ReactiveRedisTemplate<String, ServiceMetadata> serviceMetadataTemplate;

    private final SimpleEvaluationContext evaluationContext = SimpleEvaluationContext.forReadOnlyDataBinding()
            .withInstanceMethods().build();

    /**
     * {@link ServiceLocatorProperties}作为模版，serviceMetadataTemplate获取数据作为输入，构建运行时路由
     *
     * @return 运行时路由
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return serviceMetadataTemplate
                .opsForHash()
                .entries(SERVICE_METADATA_KEY)
                .map(metadataEntry -> {
                    String serviceId = (String) metadataEntry.getKey();
                    ServiceMetadata metadata = (ServiceMetadata) metadataEntry.getValue();
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("build service route: serviceId = {}, service-metadata = {}", serviceId, metadata);
                    }

                    return buildRoute(serviceId, metadata);
                });
    }

    /**
     * 构建路由
     *
     * @param serviceId 服务ID
     * @param metadata  服务元数据
     * @return 构建路由
     */
    private RouteDefinition buildRoute(String serviceId, ServiceMetadata metadata) {
        RouteDefinition routeDefinition = new RouteDefinition();
        SpelExpressionParser parser = new SpelExpressionParser();

        routeDefinition.setId(properties.getRouteIdPrefix() + "-" + serviceId);
        routeDefinition.getMetadata().put(ServiceMetadata.ROUTE_METADATA_KEY, metadata);

        Expression urlExpr = parser.parseExpression(properties.getUrlExpression());
        String uri = urlExpr.getValue(this.evaluationContext, metadata, String.class);
        routeDefinition.setUri(URI.create(Objects.requireNonNull(uri)));

        buildRoutePredicates(routeDefinition, parser, metadata);
        buildRouteFilters(routeDefinition, parser, metadata);

        return routeDefinition;
    }

    /**
     * 构建路由匹配
     *
     * @param routeDefinition 路由定义
     * @param parser          共用正则编译器
     * @param metadata        服务元数据
     */
    private void buildRoutePredicates(RouteDefinition routeDefinition, SpelExpressionParser parser, ServiceMetadata metadata) {
        for (PredicateDefinition original : this.properties.getPredicates()) {
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setName(original.getName());
            for (Map.Entry<String, String> entry : original.getArgs()
                    .entrySet()) {
                String value = getValueFromExpr(parser, metadata, entry.getValue());
                predicate.addArg(entry.getKey(), value);
            }
            routeDefinition.getPredicates().add(predicate);
        }
    }

    /**
     * 构建路由过滤器
     *
     * @param routeDefinition 路由定义
     * @param parser          共用正则编译器
     * @param metadata        服务元数据
     */
    private void buildRouteFilters(RouteDefinition routeDefinition, SpelExpressionParser parser, ServiceMetadata metadata) {
        for (FilterDefinition original : this.properties.getFilters()) {
            FilterDefinition filter = new FilterDefinition();
            filter.setName(original.getName());
            for (Map.Entry<String, String> entry : original.getArgs()
                    .entrySet()) {
                String value = getValueFromExpr(parser, metadata, entry.getValue());
                filter.addArg(entry.getKey(), value);
            }
            routeDefinition.getFilters().add(filter);
        }
    }


    private String getValueFromExpr(SpelExpressionParser parser, ServiceMetadata metadata, String express) {
        try {
            Expression valueExpr = parser.parseExpression(express);
            return valueExpr.getValue(evaluationContext, metadata, String.class);
        } catch (ParseException | EvaluationException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to parse " + express, e);
            }
            throw e;
        }
    }
}
