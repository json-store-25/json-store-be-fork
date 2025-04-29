package deepdive.jsonstore.domain.Stock.service;

import deepdive.jsonstore.domain.Stock.entity.Stock;
import deepdive.jsonstore.domain.Stock.repository.StockRepository;
import deepdive.jsonstore.domain.product.entity.Product;
import deepdive.jsonstore.domain.product.exception.ProductException;
import deepdive.jsonstore.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Service
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private static final int RECORD_COUNT = 3;

    /** 재고 차감 */
    @Transactional
    public void decrementStockByUlid(byte[] ProductUlid, long delta) {
        // 필요한 스톡레코드 모두 로드
        List<Stock> selected = new ArrayList<>();
        int totalQuantity = 0;

        // 필요한 레코드들을 스킵락으로 가져옴
        // 락 획득간에 발생 가능한 예외
        // 락이 안걸린 레코드 없음? 근데 그전에 아토믹 인티저로 처리하니까..
        while (totalQuantity < delta) {
            var stock = stockRepository.findByProductUlidWithSkipLock(ProductUlid)
                    .orElseThrow(ProductException.ProductNotFoundException::new);
            selected.add(stock);
            totalQuantity += stock.getQuantity();
        }

        // 차감
        for (var stock : selected) {
            if (delta > stock.getQuantity()) {
                delta -= stock.getQuantity();
                stock.updateQuantity(0);
            } else {
                stock.updateQuantity(stock.getQuantity() - delta);
            }
        }

//        // 차감
//        if (delta != 0) {
//            throw new RuntimeException();
//        }
    }

    /** 스톡 계산 */
    public long calculateStock(byte[] orderUlid) {
        return stockRepository.calculateTotalByProductUlid(orderUlid);
    }

    /** 스톡 분배 */
    @Transactional
    public void spreadStock(Product product) {
        List<Stock> stocks = stockRepository.findByProductUlid(product.getUlid());

        if (stocks.isEmpty()) {
            throw new ProductException.ProductNotFoundException();
        }

        var sum = stocks.stream().mapToLong(stock -> stock.getQuantity())
                .sum();

        // 재고 레코드
        while (stocks.size() > RECORD_COUNT) {
            stockRepository.delete(stocks.removeFirst());
        }

        var productUild = stocks.getFirst().getProductUlid();
        while (stocks.size() < RECORD_COUNT) {
            stocks.add(stockRepository.save(
                    Stock.builder()
                    .productUlid(productUild)
                    .build()
            ));
        }

        stocks.stream().forEach(stock -> stock.updateQuantity(sum / RECORD_COUNT));
        stocks.getFirst().updateQuantity(stocks.getFirst().getQuantity() + sum % RECORD_COUNT);
    }


//    @Scheduled(cron = "0 0 0 * * *")
//    public void stockScheduleHandler() {
//        // 대용량이라 스프링배치 필요할 듯 함.
//        productRepository.findAll().stream()
//                        .forEach(this::spreadStock);
//    }
}
