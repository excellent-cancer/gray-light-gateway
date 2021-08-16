package gray.light.gateway.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

/**
 * 网管服务入口
 *
 * @author XyParaCrim
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableRedisWebSession
public class GrayLightGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrayLightGatewayApplication.class, args);
    }

}
