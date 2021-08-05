package gray.light.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务定位配置属性
 *
 * @author XyParaCrim
 */
@Data
@ConfigurationProperties("gray.light.gateway.service.locator")
public class ServiceLocatorProperties {

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 路由ID前缀
     */
    private String routeIdPrefix;

    /**
     * url正则
     */
    private String urlExpression = "'lb://'+serviceId";

    /**
     * 路由默认匹配
     */
    private List<PredicateDefinition> predicates = new ArrayList<>();

    /**
     * 路由默认过滤器
     */
    private List<FilterDefinition> filters = new ArrayList<>();
}
