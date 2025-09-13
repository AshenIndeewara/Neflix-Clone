package lk.ijse.netflix.service;

import lk.ijse.netflix.entity.MyList;
import org.springframework.data.domain.Page;

public interface MyListService {
    String addMyList(String userId, String movieId);
    String removeMyList(String userId, String id);
    Page<MyList> getMyList(String userId, int page, int size);
}
