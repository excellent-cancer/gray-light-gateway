package gray.light.gateway.definition;

import lombok.Data;

/**
 * 服务元数据，用于控制服务访问、监控等
 *
 * @author XyParaCrim
 */
@Data
public class ServiceMetadata {

    public static final String ROUTE_METADATA_KEY = "route.service-metadata";

    /**
     * 是否开启
     */
    private boolean enable;

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 访问路径
     */
    private String accessPath;
}