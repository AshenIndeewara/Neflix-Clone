package lk.ijse.netflix.contoller;

import lk.ijse.netflix.dto.ApiResponse;
import lk.ijse.netflix.dto.MovieDto;
import lk.ijse.netflix.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/movies")
@RequiredArgsConstructor
@CrossOrigin
public class MovieController {
    private final MovieService movieService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addMovie(@RequestBody MovieDto movieDto) {
        System.out.println("Adding movie: " + movieDto);
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieService.addMovie(movieDto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateMovie(@PathVariable String id, @RequestBody MovieDto movieDto) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieService.updateMovie(id, movieDto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteMovie(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieService.deleteMovie(id)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllMovies(
            @RequestParam(defaultValue = "0") int page,   // page starts at 0
            @RequestParam(defaultValue = "10") int size   // 10 movies per page)
    ){

        return ResponseEntity.ok(new ApiResponse(200, "OK", movieService.getMovies(page, size)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieById(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieService.getMovieById(id)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/genres/{genre}")
    public ResponseEntity<ApiResponse> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieService.getMoviesByGenre(genre)));
    }

}
