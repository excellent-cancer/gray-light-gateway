package gray.light.gateway.config;

import floor.redis.serializer.RedisSerializationContexts;
import gray.light.gateway.model.ServiceMetadata;
import gray.light.gateway.properties.ServiceLocatorProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@Configuration
@EnableRedisWebSession
@EnableConfigurationProperties(ServiceLocatorProperties.class)
public class GrayLightAutoConfiguration {

    public static final String REDIS_KEY_PREFIX = "gateway";

    /**
     * 服务元数据redisTemplate
     *
     * @param connectionFactory 连接工厂
     * @return 服务元数据redisTemplate
     */
    @Bean
    public ReactiveRedisTemplate<String, ServiceMetadata> serviceMetadataTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisTemplate<>(connectionFactory, RedisSerializationContexts.withJackson2Json(REDIS_KEY_PREFIX, ServiceMetadata.class));
    }
}
