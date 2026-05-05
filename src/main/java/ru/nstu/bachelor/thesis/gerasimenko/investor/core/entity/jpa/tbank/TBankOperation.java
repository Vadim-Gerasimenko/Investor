package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.tbank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.OperationState;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.dictionary.OperationType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "tbank", name = "operations",
        indexes = {
                @Index(name = "idx_tbank_operations_account_id", columnList = "account_id"),
                @Index(name = "idx_tbank_operations_instrument_uid", columnList = "instrument_uid"),
                @Index(name = "idx_tbank_operations_date", columnList = "operation_date"),
                @Index(name = "idx_tbank_operations_type", columnList = "operation_type"),
                @Index(name = "idx_tbank_operations_account_date", columnList = "account_id, operation_date DESC"),
                @Index(name = "idx_tbank_operations_parent", columnList = "parent_operation_id")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TBankOperation {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_operation_id")
    private TBankOperation parentOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private TBankAccount account;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_type", nullable = false)
    private OperationType operationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state", nullable = false)
    private OperationState state;

    @Column(name = "payment_value", nullable = false)
    private Long paymentValue;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "quantity_rest", nullable = false)
    private Long quantityRest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrument_uid")
    private TBankInstrument instrument;

    @Column(name = "operation_date", nullable = false)
    private LocalDateTime operationDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TBankTrade> trades = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}