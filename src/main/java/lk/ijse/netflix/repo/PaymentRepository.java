package lk.ijse.netflix.repo;

import lk.ijse.netflix.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    boolean existsByUserIdAndStatus(UUID user_id, String status);
}