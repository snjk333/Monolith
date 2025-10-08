package com.oleksandr.monolith.Wallet.EntityRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
}
