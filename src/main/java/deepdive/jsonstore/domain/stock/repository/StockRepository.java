package deepdive.jsonstore.domain.stock.repository;

import deepdive.jsonstore.domain.stock.entity.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    /** 스킵락이 적용된 조회 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = """
            SELECT s
            FROM stocks s
            WHERE s.productUlid = :productUlid
            ORDER BY s.quantity
            FOR UPDATE SKIP LOCK
            """,
            nativeQuery = true
    )
    Optional<Stock> findByProductUlidWithSkipLock(@Param("productUlid") byte[] productUlid);

    List<Stock> findByProductUlid(byte[] productUlid);

    @Query("""
            SELECT SUM(s.quantity)
            FROM Stock s
            WHERE s.productUlid = :productUlid
            """)
    Long calculateTotalByProductUlid(@Param("productUlid") byte[] productUlid);

}
