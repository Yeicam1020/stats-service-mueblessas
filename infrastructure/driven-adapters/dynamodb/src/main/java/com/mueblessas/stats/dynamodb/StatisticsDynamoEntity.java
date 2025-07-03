package com.mueblessas.stats.dynamodb;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.mueblessas.stats.model.statistics.Statistics;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Data
public class StatisticsDynamoEntity {
    private Long timestamp;
    private Integer totalContactoClientes;
    private Integer motivoReclamo;
    private Integer motivoGarantia;
    private Integer motivoDuda;
    private Integer motivoCompra;
    private Integer motivoFelicitaciones;
    private Integer motivoCambio;
    private String hash;

    @DynamoDbPartitionKey
    public Long getTimestamp() {
        return timestamp;
    }
    public static StatisticsDynamoEntity fromDomain(Statistics stats) {
        StatisticsDynamoEntity e = new StatisticsDynamoEntity();
        BeanUtils.copyProperties(stats, e);
        if (e.getTimestamp() == null) {
            e.setTimestamp(System.currentTimeMillis());
        }
        return e;
    }

}
