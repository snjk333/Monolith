package com.oleksandr.monolith.Wallet.Service;

import com.oleksandr.monolith.Wallet.DTO.DepositRequestDTO;
import com.oleksandr.monolith.Wallet.DTO.WalletDTO;
import com.oleksandr.monolith.Wallet.EntityRepo.Wallet;
import com.oleksandr.monolith.Wallet.EntityRepo.WalletRepository;
import com.oleksandr.monolith.Wallet.util.WalletMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletMapper walletMapper;
    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletMapper walletMapper, WalletRepository walletRepository) {
        this.walletMapper = walletMapper;
        this.walletRepository = walletRepository;
    }

    @Override
    public WalletDTO processDeposit(Wallet userWallet, DepositRequestDTO paymentRequestDTO) {
        BigDecimal newBalance = userWallet.getBalance().add(paymentRequestDTO.getAmount());
        userWallet.setBalance(newBalance);

        return walletMapper.mapToDto(userWallet);
    }

    @Override
    public Wallet createEmptyWallet() {
        Wallet wallet = new Wallet();
        return walletRepository.save(wallet);
    }
}
