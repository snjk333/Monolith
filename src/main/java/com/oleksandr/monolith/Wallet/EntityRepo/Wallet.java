package com.oleksandr.monolith.Wallet.EntityRepo;

import com.oleksandr.monolith.User.EntityRepo.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
public class Wallet {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Wallet() {
        this.balance = BigDecimal.ZERO;
    }
}