package com.oleksandr.monolith.Wallet.Service;

import com.oleksandr.monolith.Wallet.DTO.DepositRequestDTO;
import com.oleksandr.monolith.Wallet.DTO.WalletDTO;
import com.oleksandr.monolith.Wallet.EntityRepo.Wallet;

public interface WalletService {

    WalletDTO processDeposit(Wallet userWallet, DepositRequestDTO paymentRequestDTO);

    Wallet createEmptyWallet();
}
