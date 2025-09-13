package lk.ijse.netflix.service;

import lk.ijse.netflix.entity.Payment;
import org.springframework.data.domain.Page;

public interface PayHereService {

    String generateSignature(String orderId, double amount, String currency);

    String hash(String input) throws Exception;

    Page<Payment> getAllPayments(int page, int size);

    String handlePaymentNotification(String capturedAmount, String orderId, String statusCode, String md5sig) throws Exception;

    void save(Payment payment);

    Long getTotalRevenue();

    Long getMonthlyRevenue(int month, int year);

    Long getRevenueByPlan(String plan);

    Double getAverageRevenuePerUser();

    Double getPaymentSuccessRate();
}
