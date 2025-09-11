package lk.ijse.netflix.repo;

import lk.ijse.netflix.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Payment findPaymentByOrderId(String orderId);

    Payment findPaymentByUserID(String userID);

    //Long getTotalRevenue();
    @Query(value = "SELECT SUM(amount) FROM Payment", nativeQuery = true)
    Long getTotalRevenue();

    //Long getMonthlyRevenue(int month, int year);
    @Query(value = "SELECT SUM(amount) FROM Payment WHERE MONTH(createdAt) = ?1 AND YEAR(createdAt) = ?2", nativeQuery = true)
    Long getMonthlyRevenue(int month, int year);

    Long getRevenueByPlan(String plan);

    //Double getAverageRevenuePerUser();
    @Query(value = "SELECT AVG(amount) FROM Payment", nativeQuery = true)
    Double getAverageRevenuePerUser();

    Long countByStatus(String completed);
}