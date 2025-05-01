package deepdive.jsonstore.domain.stock.dto;

import deepdive.jsonstore.domain.stock.service.OrderProductDto;
import lombok.Builder;

import java.util.List;


@Builder
public record StockEventDto (
        byte[] orderUlid,
        List<OrderProductDto> orderProductDtos
){}