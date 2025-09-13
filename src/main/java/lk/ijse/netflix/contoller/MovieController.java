package lk.ijse.netflix.contoller;

import lk.ijse.netflix.dto.ApiResponse;
import lk.ijse.netflix.dto.MovieDto;
import lk.ijse.netflix.dto.UserData;
import lk.ijse.netflix.service.MyListService;
import lk.ijse.netflix.service.impl.MovieServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/movies")
@RequiredArgsConstructor
@CrossOrigin
public class MovieController {
    private final MovieServiceIMPL movieServiceIMPL;
    private final MyListService myListService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addMovie(@RequestBody MovieDto movieDto) {
        System.out.println("Adding movie: " + movieDto);
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieServiceIMPL.addMovie(movieDto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateMovie(@PathVariable String id, @RequestBody MovieDto movieDto) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieServiceIMPL.updateMovie(id, movieDto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteMovie(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieServiceIMPL.deleteMovie(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/views-count")
    public ResponseEntity<ApiResponse> getMostViewedMovies() {
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieServiceIMPL.getTotalViews()));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllMovies(
            @RequestParam(defaultValue = "0") int page,   // page starts at 0
            @RequestParam(defaultValue = "10") int size   // 10 movies per page)
    ){

        return ResponseEntity.ok(new ApiResponse(200, "OK", movieServiceIMPL.getMovies(page, size)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMovieById(@PathVariable String id) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieServiceIMPL.getMovieById(id)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/genres/{genre}")
    public ResponseEntity<ApiResponse> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(new ApiResponse(200, "OK", movieServiceIMPL.getMoviesByGenre(genre)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("addMyList/{id}")
    public ResponseEntity<ApiResponse> addMyList(@PathVariable String id) {
        System.out.println("Movie ID to add to My List: " + id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData principal = (UserData) auth.getPrincipal();
        return ResponseEntity.ok(new ApiResponse(200, "OK", myListService.addMyList(principal.getUserId(), id)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("removeMyList/{id}")
    public ResponseEntity<ApiResponse> removeMyList(@PathVariable String id) {
        System.out.println("Removing movie from my list: " + id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData principal = (UserData) auth.getPrincipal();
        return ResponseEntity.ok(new ApiResponse(200, "OK", myListService.removeMyList(principal.getUserId(), id)));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("getMyList")
    public ResponseEntity<ApiResponse> getMyList(
            @RequestParam(defaultValue = "0") int page,   // page starts at 0
            @RequestParam(defaultValue = "10") int size   // 10 movies per page)
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserData principal = (UserData) auth.getPrincipal();
        System.out.println(principal.getUserId());
        return ResponseEntity.ok(new ApiResponse(200, "OK", myListService.getMyList(principal.getUserId(), page, size)));
    }


}
