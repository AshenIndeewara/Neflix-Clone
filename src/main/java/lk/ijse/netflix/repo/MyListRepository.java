package lk.ijse.netflix.repo;

import lk.ijse.netflix.entity.MyList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyListRepository extends JpaRepository<MyList, String> {
    Page<MyList> findAllByUserId(String userId, Pageable pageable);

    void deleteById(String id);
}

