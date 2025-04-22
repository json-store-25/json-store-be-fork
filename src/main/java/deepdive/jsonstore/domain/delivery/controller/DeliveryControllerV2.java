package deepdive.jsonstore.domain.delivery.controller;

import deepdive.jsonstore.domain.delivery.dto.DeliveryRegRequestDTO;
import deepdive.jsonstore.domain.delivery.service.DeliveryService;
import deepdive.jsonstore.domain.delivery.service.DeliveryServiceV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class DeliveryControllerV2 {

    private final DeliveryServiceV2 deliveryService;

    //배송지 등록
    @PostMapping("/delivery")
    public ResponseEntity<?> createDelivery(@AuthenticationPrincipal(expression = "uid") UUID memberUid, @RequestBody DeliveryRegRequestDTO deliveryRegDTO) {
        log.info("배송지 등록 요청: {}", memberUid.toString());
        deliveryService.createDelivery(memberUid, deliveryRegDTO);
        return ResponseEntity.created(URI.create("/api/v1/delivery")).build(); 
    }

    //배송지 삭제
    @DeleteMapping("/delivery/{ulid}")
    public ResponseEntity<?> deleteDelivery(@AuthenticationPrincipal(expression = "uid") UUID memberUid, @PathVariable UUID ulid){
        log.info("배송지 삭제 요청: {}", memberUid.toString());
        deliveryService.deleteDelivery(memberUid, ulid);
        return ResponseEntity.ok().build();
    }

    //배송지 조회
    @GetMapping("/delivery")
    public ResponseEntity<?> getDelivery(@AuthenticationPrincipal(expression = "uid") UUID memberUid){
        log.info("배송지 조회 요청: {}", memberUid.toString());
        return ResponseEntity.ok(deliveryService.getDelivery(memberUid));
  }

    //배송지 수정
    @PutMapping("/delivery/{ulid}")
    public ResponseEntity<?> updateDelivery(@AuthenticationPrincipal(expression = "uid") UUID memberUid, @RequestBody DeliveryRegRequestDTO deliveryRegDTO, @PathVariable UUID ulid){
        log.info("배송지 수정 요청: {}", memberUid.toString());
        deliveryService.updateDelivery(memberUid, ulid, deliveryRegDTO);
        return ResponseEntity.ok(URI.create("/api/v2/delivery"));
    }

    //기본 배송지 등록
    @PatchMapping("/delivery/default/{deliveryUlid}")
    public ResponseEntity<?> setDefaultDelivery(@AuthenticationPrincipal(expression = "uid") UUID memberUid, @PathVariable UUID deliveryUlid){
        log.info("배송지 기본 등록 요청: {}", deliveryUlid.toString());
        deliveryService.setDeliveryDefault(memberUid, deliveryUlid);
        return ResponseEntity.noContent().build();
    }
}
