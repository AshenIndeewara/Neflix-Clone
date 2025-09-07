package lk.ijse.netflix.contoller;

import io.jsonwebtoken.Header;
import lk.ijse.netflix.dto.ApiResponse;
import lk.ijse.netflix.dto.PaymentRequest;
import lk.ijse.netflix.dto.PaymentResponse;
import lk.ijse.netflix.entity.Payment;
import lk.ijse.netflix.entity.Role;
import lk.ijse.netflix.entity.User;
import lk.ijse.netflix.repo.PaymentRepository;
import lk.ijse.netflix.repo.UserRepository;
import lk.ijse.netflix.service.PayHereService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("v1/api/payments")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentController {

    private final PayHereService payHereService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @PostMapping
    public PaymentResponse createPayment(@RequestBody PaymentRequest request, Principal principal) {
        System.out.println(principal);
        String userEmail = principal.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));


        String orderId = "ORDER_" + System.currentTimeMillis();
        String signature = payHereService.generateSignature(orderId, request.getAmount(), request.getCurrency());
        System.out.println(" Generated signature: " + signature);

        Payment payment = Payment.builder()
                .orderId(orderId)
                .plan(request.getPlan())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status("PENDING")
                .customerEmail(user.getEmail())
                .user(user)
                .build();

        paymentRepository.save(payment);
        System.out.println("💾 Payment saved: " + payment);

        return new PaymentResponse(orderId, signature, user.getEmail());
    }

    @PostMapping("/notify")
    public String notifyPayment(@RequestParam String order_id, @RequestParam String status_code, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        System.out.println("Payment notification received: order_id=" + order_id + ", status_code=" + status_code);
        String userEmail = userDetails.getUsername();
        System.out.println("Checking payment status for user: " + userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Payment payment = paymentRepository.findAll()
                .stream()
                .filter(p -> p.getOrderId().equals(order_id))
                .findFirst()
                .orElse(null);

        if (payment != null) {
            if ("2".equals(status_code)) {
                payment.setStatus("SUCCESS");
                userRepository.updateUserRoleById(user.getId(), Role.USER);
            } else {
                payment.setStatus("FAILED");
            }
            paymentRepository.save(payment);
        }
        return "OK";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,   // page starts at 0
            @RequestParam(defaultValue = "10") int size   // 10 payments per page
    ) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", payHereService.getAllPayments(page, size)));
    }


    @GetMapping("/check")
    public ResponseEntity<ApiResponse> checkPaymentStatus(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        String userEmail = userDetails.getUsername();
        System.out.println("Checking payment status for user: " + userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Boolean payment = paymentRepository.existsByUserIdAndStatus(user.getId(), "SUCCESS");
        System.out.println("Payment status for user " + userEmail + ": " + payment);
        if (payment) {
            return ResponseEntity.ok(new ApiResponse(
                    200,
                    "OK",
                    true));
        } else {
            return ResponseEntity.ok(new ApiResponse(
                    404,
                    "No payment found for user",
                    false));
        }
    }
}
