package com.oleksandr.monolith.User.Service;

import com.oleksandr.monolith.User.DTO.UserProfileResponseDTO;
import com.oleksandr.monolith.User.DTO.UserSummaryDTO;
import com.oleksandr.monolith.User.DTO.UserUpdateRequestDTO;
import com.oleksandr.monolith.User.EntityRepo.User;
import com.oleksandr.monolith.Wallet.DTO.DepositRequestDTO;
import com.oleksandr.monolith.Wallet.DTO.WalletDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserService {

    @Transactional
    User getOrCreateUser(UUID userId);

    UserProfileResponseDTO getUserProfile(UUID userId);

    UserSummaryDTO updateUserProfile(UUID userId, UserUpdateRequestDTO request);

    WalletDTO Deposit(UUID userId, DepositRequestDTO wallet);
}
