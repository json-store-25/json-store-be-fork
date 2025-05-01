package deepdive.jsonstore.domain.stock.service;


import deepdive.jsonstore.domain.order.entity.OrderProduct;

import java.util.List;

public record OrderProductDto (
        byte[] productUlid,
        long quantity

){

}
