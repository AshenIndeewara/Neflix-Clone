package lk.ijse.netflix.service.impl;

import lk.ijse.netflix.entity.Payment;
import lk.ijse.netflix.repo.PaymentRepository;
import lk.ijse.netflix.service.PayHereService;
import lk.ijse.netflix.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class PayHereServiceIMPL implements PayHereService {
    @Value("${payhere.merchant.id}")
    private  String merchantId;

    @Value("${payhere.merchant.secret}")
    private  String appSecret;

    private final PaymentRepository paymentRepository;
    private final UserService userService;

    @Override
    public String generateSignature(String orderId, double amount, String currency) {
        try {
            String data = merchantId + orderId + String.format("%.2f", amount) + currency + hash(appSecret).toUpperCase();
            System.out.println(data);
            return hash(data).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }
    @Override
    public String hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    @Override
    public Page<Payment> getAllPayments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findAll(pageable);
    }

    @Override
    public String handlePaymentNotification(String capturedAmount, String orderId, String statusCode, String md5sig) throws Exception {
        Payment payment = paymentRepository.findPaymentByOrderId(orderId);
        if (payment == null) {
            return "Payment not found";
        }
        if ("2".equals(statusCode)) {
            payment.setStatus("COMPLETED");
            paymentRepository.save(payment);
            System.out.println("💾 Payment updated to COMPLETED: " + payment);
            userService.updateUserRole("USER", payment.getUserID(), payment.getPlan());
            return "Payment completed";
        } else {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            return "Payment failed";
        }
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public Long getTotalRevenue() {
        return paymentRepository.getTotalRevenue();
    }

    @Override
    public Long getMonthlyRevenue(int month, int year) {
        return paymentRepository.getMonthlyRevenue(month, year);
    }

    @Override
    public Long getRevenueByPlan(String plan) {
        return paymentRepository.getRevenueByPlan(plan);
    }

    @Override
    public Double getAverageRevenuePerUser() {
        return paymentRepository.getAverageRevenuePerUser();
    }

    @Override
    public Double getPaymentSuccessRate() {
        Long totalPayments = paymentRepository.count();
        Long successfulPayments = paymentRepository.countByStatus("COMPLETED");
        if (totalPayments == 0) return 0.0;
        return (successfulPayments.doubleValue() / totalPayments) * 100;
    }
}
