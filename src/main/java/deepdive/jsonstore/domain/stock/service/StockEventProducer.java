package deepdive.jsonstore.domain.stock.service;

import deepdive.jsonstore.common.exception.CommonException;
import deepdive.jsonstore.domain.order.entity.OrderProduct;
import deepdive.jsonstore.domain.stock.dto.StockEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockEventProducer {

    private static final String TOPIC = "example-topic";
    private static final int PARTITION_COUNT = 1;

    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    public void sendEvent(StockEventDto stockEventDto) {
        // 요청 메시지와 reply-topic 정보를 설정
        ProducerRecord<String, Object> record = new ProducerRecord<>("request-topic", null, stockEventDto);
//        record.headers().add(KafkaHeaders.REPLY_TOPIC);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "reply-topic".getBytes()));


        // 요청-응답 전송
        RequestReplyFuture<String, Object, Object> future = replyingKafkaTemplate.sendAndReceive(record);

        // 응답 대기 및 반환
        try {
            var response = future.get(10, TimeUnit.SECONDS);  // 10초 타임아웃
            log.info("응답 수신: {}", response.value());
        } catch (Exception e) {
            log.info("응답 수신 실패: {}", e);
            throw new CommonException.InternalServerException();
        }
    }
}
