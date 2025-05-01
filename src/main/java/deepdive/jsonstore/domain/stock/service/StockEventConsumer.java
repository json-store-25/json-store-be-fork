package deepdive.jsonstore.domain.stock.service;

import deepdive.jsonstore.common.config.KafkaConfig;
import deepdive.jsonstore.domain.product.exception.ProductException;
import deepdive.jsonstore.domain.stock.dto.StockEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockEventConsumer {

    private final StockService stockService;

    @KafkaListener(topics = "request-topic", groupId = "kafka-reply-group"
           ,containerFactory = "kafkaListenerContainerFactory"
    )
    @SendTo
    public Object handleRequest(ConsumerRecord<String, Object> record) {
        StockEventDto event = (StockEventDto) record.value();  // 또는 StockEventDto로 캐스팅
        log.info("수신된 메시지 value = {}", event);
        log.info("헤더 정보 = {}", record.headers());
//        재고 차감 결과 이벤트를 "주문 단위로 묶어서" 응답
        event.orderProductDtos().forEach(op-> {
            try {
                stockService.decrementStockByUlid(op.productUlid(), op.quantity());
            } catch (Exception e) {
                // "실패시 지금까지 했던 것 복구" 이 주문은 실패한 주문. 다른 상품을 복구 시켜야한다.
                // 다른 주문에 어떻게 전파할것인가

            }
        });


        // 여기서 가공해도 되고
        return Map.of("result", true);
//        return record.value;
    }
}
