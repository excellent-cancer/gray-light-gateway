package gray.light.gateway.config;

import gray.light.gateway.model.ServiceMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableRedisWebSession
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
        RedisSerializer<String> keySerializer = new PrefixStringRedisSerializer(REDIS_KEY_PREFIX, StandardCharsets.UTF_8);
        RedisSerializer<ServiceMetadata> valueSerializer = new Jackson2JsonRedisSerializer<>(ServiceMetadata.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, ServiceMetadata> builder = RedisSerializationContext.newSerializationContext();
        RedisSerializationContext<String, ServiceMetadata> context = builder.key(keySerializer).value(valueSerializer)
                .hashKey(keySerializer).hashValue(valueSerializer).build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    @RequiredArgsConstructor
    private static class PrefixStringRedisSerializer extends StringRedisSerializer {

        private final String prefix;

        public PrefixStringRedisSerializer(String prefix, Charset charset) {
            super(charset);
            Assert.hasLength(prefix, "prefix must not empty");
            this.prefix = prefix;
        }

        @Override
        public String deserialize(byte[] bytes) {
            return super.deserialize(bytes).substring(prefix.length() + 1);
        }

        @Override
        public byte[] serialize(String string) {
            return super.serialize(prefix + ":" + string);
        }
    }

}
