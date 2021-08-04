package gray.light.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网管服务入口
 *
 * @author XyParaCrim
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GrayLightGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrayLightGatewayApplication.class, args);
    }

}
