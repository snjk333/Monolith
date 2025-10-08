package com.oleksandr.monolith.Wallet.util;

import com.oleksandr.monolith.User.DTO.UserProfileResponseDTO;
import com.oleksandr.monolith.Wallet.DTO.WalletDTO;
import com.oleksandr.monolith.Wallet.EntityRepo.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletDTO mapToDto(Wallet wallet) {
        if (wallet == null) throw new IllegalArgumentException("Wallet entity cannot be null");

        return WalletDTO.builder()
                .balance(wallet.getBalance())
                .build();
        }
}
