package lk.ijse.netflix.service;

import lk.ijse.netflix.dto.MovieDto;
import lk.ijse.netflix.entity.Movie;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieService {
    String addMovie(MovieDto movieDto);
    Page<Movie> getMovies(int page, int size);
    Movie getMovieById(String id);
    List<Movie> getMoviesByGenre(String genre);
    String updateMovie(String id, MovieDto movieDto);
    String deleteMovie(String id);
    Long getTotalViews();
    List<Movie> getMostViewedMovies();
}
