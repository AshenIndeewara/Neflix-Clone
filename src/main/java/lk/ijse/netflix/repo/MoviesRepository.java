package lk.ijse.netflix.repo;

import lk.ijse.netflix.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface MoviesRepository extends JpaRepository<Movie, Integer> {
    Movie findById(String id);
    List<Movie> findAllMoviesByGenresContaining(String genre);
    @Modifying
    @Transactional
    @Query("UPDATE Movie m SET m.views = :views WHERE m.id = :id")
    int updateMovieViewsById(@Param("id") String id, @Param("views") Long views);

    //count all views
    @Query("SELECT SUM(m.views) FROM Movie m")
    Long countAllViews();
    List<Movie> findTop10ByOrderByViewsDesc();

    Movie findByTitleAndVideoUrl(String title, String videoUrl);
}
