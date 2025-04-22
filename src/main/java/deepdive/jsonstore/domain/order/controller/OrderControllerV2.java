package deepdive.jsonstore.domain.order.controller;

import de.huxhorn.sulky.ulid.ULID;
import deepdive.jsonstore.common.util.UlidUtil;
import deepdive.jsonstore.domain.order.dto.*;
import deepdive.jsonstore.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/orders")
public class OrderControllerV2 {

    private final OrderService orderService;

    /** 주문 생성 */
    @PostMapping
    public ResponseEntity<Void> createOrder(
            @AuthenticationPrincipal(expression="uid") String memberUid,
            @RequestBody OrderRequest orderRequest) {
        var memberUlid = ULID.parseULID(memberUid);
        var orderUid = orderService.createOrder(memberUlid.toBytes(), orderRequest);
        return ResponseEntity.created(
                URI.create("/api/v1/orders/" + orderUid.toString())
        ).build();
    }

    /** 주문 조회 */
    @GetMapping("/{orderUid}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("orderUid") String orderUid) {
        var ulid = ULID.parseULID(orderUid);
        return ResponseEntity.ok(orderService.getOrderResponse(ulid.toBytes()));
    }

    /** 주문 페이지 조회 */
    @GetMapping("")
    public ResponseEntity<Page<OrderResponse>> getOrder(
            @AuthenticationPrincipal(expression="uid") String memberUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String direction

    ) {
        var pageRequest = PageRequest.of(
                0,
                10,
                Sort.by(direction, "createdAt")
        );
        var memberUlid = ULID.parseULID(memberUid);
        return ResponseEntity.ok(orderService.getOrderResponsesByPage(memberUlid.toBytes(), pageRequest));
    }

    /** PG 결제 승인 요청 */
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestBody ConfirmRequest confirmRequest) {
        orderService.confirmOrder(confirmRequest);
        return ResponseEntity.ok().build();
    }

    /** 주문 취소 */
    @PostMapping("/{orderUid}/cancel")
    public ResponseEntity<?> cancel(@PathVariable("orderUid") String orderUid) {
        var orderUlid = ULID.parseULID(orderUid);
        orderService.cancelOrderBeforeShipment(orderUlid.toBytes());
        return ResponseEntity.ok().build();
    }

    /** 사용자 배송지 변경 */
    @PutMapping("/{orderUid}/delivery/{deliveryUid}")
    public ResponseEntity<?> updateOrderDelivery(
            @PathVariable("orderUid") String orderUid,
            @PathVariable("deliveryUid") String deliveryUid
    ) {

        var orderUlid = ULID.parseULID(orderUid);
        var deliveryUlid = ULID.parseULID(deliveryUid);
        orderService.updateOrderDeliveryBeforeShipping(orderUlid.toBytes(), deliveryUlid.toBytes());
        return ResponseEntity.ok().build();
    }
}