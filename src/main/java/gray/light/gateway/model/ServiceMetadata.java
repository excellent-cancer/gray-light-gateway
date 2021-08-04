package gray.light.gateway.model;

import lombok.Data;

/**
 * 服务元数据，用于控制服务访问、监控等
 *
 * @author XyParaCrim
 */
@Data
public class ServiceMetadata {

    /**
     * 是否开启
     */
    private boolean enable;

}
