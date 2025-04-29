package deepdive.jsonstore.domain.Stock.entity;

import deepdive.jsonstore.common.util.UlidUtil;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Table(name = "stocks")
@Builder
@Getter
@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(columnDefinition = "binary(16)", nullable = false, unique = true)
    private UUID uid = UUID.randomUUID();

    @Builder.Default
    @Column(columnDefinition = "binary(16)", nullable = false, unique = true)
    private byte[] ulid = UlidUtil.createUlidBytes();

    @Column(columnDefinition = "binary(16)", nullable = false)
    private byte[] productUlid;

    private Long quantity;

    public void updateQuantity(long quantity){ this.quantity = quantity; }
}
