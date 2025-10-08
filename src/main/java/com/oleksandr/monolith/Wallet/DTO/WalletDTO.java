package com.oleksandr.monolith.Wallet.DTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletDTO {

    private BigDecimal balance;

}
