package lk.ijse.netflix.service;

import lk.ijse.netflix.dto.MovieDto;
import lk.ijse.netflix.entity.Movie;
import lk.ijse.netflix.repo.MoviesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MoviesRepository movieRepository;

    public String addMovie(MovieDto movieDto) {
        System.out.println("Received movie DTO: " + movieDto);
        Movie movie = Movie.builder()
                .title(movieDto.getTitle())
                .year(movieDto.getYear())
                .genres(movieDto.getGenres())
                .description(movieDto.getDescription())
                .imageUrl(movieDto.getImageUrl())
                .videoUrl(movieDto.getVideoUrl())
                .subtitleUrl(movieDto.getSubtitleUrl())
                .views(1L).build();
        movieRepository.save(movie);
        return "Movie added successfully";
    }

    public Page<Movie> getMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable);
    }

    public Movie getMovieById(String id) {
        Movie movie = movieRepository.findById(id);
        movieRepository.updateMovieViewsById(id, movie.getViews() + 1);
        return movie;
    }

    public List<Movie> getMoviesByGenre(String genre) {
        List<Movie> movies = movieRepository.findAllMoviesByGenresContaining(genre);
        if (movies.isEmpty()) {
            return Collections.emptyList();
        }
        return movies;
    }

    public String updateMovie(String id, MovieDto movieDto) {
        Movie existingMovie = movieRepository.findById(id);
        if (existingMovie == null) {
            return "Movie not found";
        }
        existingMovie.setTitle(movieDto.getTitle());
        existingMovie.setYear(movieDto.getYear());
        existingMovie.setGenres(movieDto.getGenres());
        existingMovie.setDescription(movieDto.getDescription());
        existingMovie.setImageUrl(movieDto.getImageUrl());
        existingMovie.setVideoUrl(movieDto.getVideoUrl());
        existingMovie.setSubtitleUrl(movieDto.getSubtitleUrl());
        existingMovie.setViews(movieDto.getViews());
        movieRepository.save(existingMovie);
        return "Movie updated successfully";
    }

    public String deleteMovie(String id) {
        Movie existingMovie = movieRepository.findById(id);
        if (existingMovie == null) {
            return "Movie not found";
        }
        movieRepository.delete(existingMovie);
        return "Movie deleted successfully";
    }
}
