package pl.kurs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books",indexes = {
        @Index(name = "idx_book_author", columnList = "author"),
        @Index(name = "idx_book_category", columnList = "book_category"),
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_category", nullable = false)
    private Category category;

    @Column(name = "page_count")
    private Integer pageCount;

}
