package lk.ijse.netflix.contoller;

import lk.ijse.netflix.dto.ApiResponse;
import lk.ijse.netflix.service.PayHereService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("public")
@RequiredArgsConstructor
@CrossOrigin
public class PublicController {
    private final PayHereService payHereService;
    @Value("${spring.datasource.url}")
    private String sqlUrl;

//    @PostMapping("notify")
//    public ResponseEntity<ApiResponse> notifyPayment(@RequestBody Map<String, Object> request) throws Exception {
//        System.out.println("Payment notification received: " + request);
//        String captured_amount = (String) request.get("captured_amount");
//        String order_id = (String) request.get("order_id");
//        String status_code = (String) request.get("status_code");
//        String md5sig = (String) request.get("md5sig");
//        System.out.println("Processing payment notification for order_id: " + order_id + ", status_code: " + status_code);
//        return ResponseEntity.ok(new ApiResponse(200, "OK", payHereService.handlePaymentNotification(captured_amount, order_id, status_code, md5sig)));
//    }
    @PostMapping(value = "/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ApiResponse> notifyPayment(
        @RequestParam("merchant_id") String merchantId,
        @RequestParam("order_id") String orderId,
        @RequestParam("payment_id") String paymentId,
        @RequestParam("captured_amount") String capturedAmount,
        @RequestParam("payhere_amount") String payhereAmount,
        @RequestParam("payhere_currency") String payhereCurrency,
        @RequestParam("status_code") String statusCode,
        @RequestParam("md5sig") String md5sig,
        @RequestParam(value = "custom_1", required = false) String custom1,
        @RequestParam(value = "custom_2", required = false) String custom2,
        @RequestParam("status_message") String statusMessage,
        @RequestParam("method") String method,
        @RequestParam("card_holder_name") String cardHolderName,
        @RequestParam("card_no") String cardNo,
        @RequestParam("card_expiry") String cardExpiry,
        @RequestParam("recurring") String recurring
    ) throws Exception {
    System.out.println("Payment notification received for order: " + orderId);

    return ResponseEntity.ok(
            new ApiResponse(
                    200,
                    "OK",
                    payHereService.handlePaymentNotification(capturedAmount, orderId, statusCode, md5sig)
            )
    );
    }



    @GetMapping("env")
    public ResponseEntity<ApiResponse> getEnv() throws IOException {
        return ResponseEntity.ok(new ApiResponse(200, "OK", sqlUrl));
    }
}
