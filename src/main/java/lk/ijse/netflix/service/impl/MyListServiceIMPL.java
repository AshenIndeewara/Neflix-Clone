package lk.ijse.netflix.service.impl;

import lk.ijse.netflix.entity.Movie;
import lk.ijse.netflix.entity.MyList;
import lk.ijse.netflix.repo.MoviesRepository;
import lk.ijse.netflix.repo.MyListRepository;
import lk.ijse.netflix.service.MyListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyListServiceIMPL implements MyListService {
    private final MyListRepository myListRepository;
    private final MoviesRepository moviesRepository;

    public String addMyList(String userId, String movieId) {
        Movie movie = moviesRepository.findById(movieId);
        if (movie == null) {
            return "Movie not found";
        }
        MyList myListEntry = lk.ijse.netflix.entity.MyList.builder()
                .userId(userId)
                .movie(movie)
                .build();
        myListRepository.save(myListEntry);
        return "Movie added to My List" ;
    }

    public String removeMyList(String userId, String id) {
        System.out.println("userId: " + userId + ", movieId: " + id);
//        Movie movie = moviesRepository.findById(movieId);
//        if (movie == null) {
//            return "Movie not found";
//        }
        myListRepository.deleteById(id);
        return "Movie removed from My List";
    }

    public Page<MyList> getMyList(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return myListRepository.findAllByUserId(userId, pageable);
    }

}
