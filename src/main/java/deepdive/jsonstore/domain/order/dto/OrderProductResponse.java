package deepdive.jsonstore.domain.order.dto;

import deepdive.jsonstore.common.util.UlidUtil;
import deepdive.jsonstore.domain.order.entity.OrderProduct;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderProductResponse(
        UUID productUid,
        String productUlid,
        String productName,
        String productImageUrl,
        int quantity,
        int amount,
        int subTotal
){
    public static OrderProductResponse from(OrderProduct orderProduct) {
        int quantity = orderProduct.getQuantity();
        int amount = orderProduct.getPrice();
        int subTotal =  quantity * amount;
        return OrderProductResponse.builder()
                .productUid(orderProduct.getUid())
                .productUlid(UlidUtil.getUlidString(orderProduct.getUlid()))
                .productName(orderProduct.getProduct().getName())
                .productImageUrl(orderProduct.getProduct().getImage())
                .quantity(quantity)
                .amount(amount)
                .subTotal(subTotal)
                .build();
    }
}
