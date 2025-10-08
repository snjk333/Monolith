package com.oleksandr.monolith.Wallet.Service;

import com.oleksandr.monolith.Wallet.DTO.DepositRequestDTO;
import com.oleksandr.monolith.Wallet.DTO.WalletDTO;
import com.oleksandr.monolith.Wallet.EntityRepo.Wallet;
import com.oleksandr.monolith.Wallet.util.WalletMapper;

import java.math.BigDecimal;

public class WalletServiceImpl implements WalletService {

    private final WalletMapper walletMapper;

    public WalletServiceImpl(WalletMapper walletMapper) {
        this.walletMapper = walletMapper;
    }

    @Override
    public WalletDTO processDeposit(Wallet userWallet, DepositRequestDTO paymentRequestDTO) {
        BigDecimal newBalance = userWallet.getBalance().add(paymentRequestDTO.getAmount());
        userWallet.setBalance(newBalance);

        return walletMapper.mapToDto(userWallet);
    }
}
