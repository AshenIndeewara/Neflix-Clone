package lk.ijse.netflix.service;

import lk.ijse.netflix.entity.Payment;
import lk.ijse.netflix.repo.PaymentRepository;
import lk.ijse.netflix.util.UpdateUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class PayHereService {
    @Value("${payhere.merchant.id}")
    private  String merchantId;

    @Value("${payhere.merchant.secret}")
    private  String appSecret;

    private final PaymentRepository paymentRepository;
    private final UserService userService;

    public String generateSignature(String orderId, double amount, String currency) {
        try {
            String data = merchantId + orderId + String.format("%.2f", amount) + currency + hash(appSecret).toUpperCase();
            System.out.println(data);
            return hash(data).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    private String hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public Page<Payment> getAllPayments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findAll(pageable);
    }

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

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    public Long getTotalRevenue() {
        return paymentRepository.getTotalRevenue();
    }

    public Long getMonthlyRevenue(int month, int year) {
        return paymentRepository.getMonthlyRevenue(month, year);
    }

    public Long getRevenueByPlan(String plan) {
        return paymentRepository.getRevenueByPlan(plan);
    }

    public Double getAverageRevenuePerUser() {
        return paymentRepository.getAverageRevenuePerUser();
    }

    public Double getPaymentSuccessRate() {
        Long totalPayments = paymentRepository.count();
        Long successfulPayments = paymentRepository.countByStatus("COMPLETED");
        if (totalPayments == 0) return 0.0;
        return (successfulPayments.doubleValue() / totalPayments) * 100;
    }
}
