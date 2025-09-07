package lk.ijse.netflix.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String plan;
    private double amount;
    private String currency;
    private String customerEmail;
}