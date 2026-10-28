package az.iktex.organization.structure.testders.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String surname;

    @Column(length = 50)
    private String country;

    @Builder.Default
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuthorBook> authorBooks = new ArrayList<>();

    // Helper metodlar
    public void addBook(Book book) {
        AuthorBook authorBook = AuthorBook.builder()
                .author(this)
                .book(book)
                .build();
        authorBooks.add(authorBook);
        book.getAuthorBooks().add(authorBook);
    }

    public void removeBook(Book book) {
        authorBooks.removeIf(ab -> ab.getBook().equals(book));
        book.getAuthorBooks().removeIf(ab -> ab.getAuthor().equals(this));
    }
}