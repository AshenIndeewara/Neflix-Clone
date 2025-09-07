package lk.ijse.netflix.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String year;
    @ElementCollection
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genre")
    private List<String> genres;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private String imageUrl;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String videoUrl;
    private String subtitleUrl;
    @Builder.Default
    private Long views = 0L;
}
