package lk.ijse.netflix.dto;

import com.clerk.backend_api.models.components.TotalCount;
import com.clerk.backend_api.models.components.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@Data
@AllArgsConstructor
public class PagedUsersResponse {
    private List<User> users;
    private Optional<TotalCount> totalCount;
    private int currentPage;
    private long totalPages;


}
