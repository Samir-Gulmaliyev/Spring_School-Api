package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);

    List<Book> findAllByTitleContainingIgnoreCase(String title);

    List<Book> findAllByPriceLessThanEqualOrderByPriceAsc(BigDecimal price);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.authorBooks ab LEFT JOIN FETCH ab.author WHERE b.id = :id")
    Optional<Book> findByIdWithAuthors(@Param("id") Long id);
}