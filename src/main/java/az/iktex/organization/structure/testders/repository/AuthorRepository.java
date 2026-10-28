package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    boolean existsByNameAndSurname(String name, String surname);

    boolean existsByNameAndSurnameAndIdNot(String name, String surname, Long id);

    List<Author> findAllByCountry(String country);

    @Query("SELECT a FROM Author a WHERE a.authorBooks IS EMPTY")
    List<Author> findAuthorsWithoutBooks();

    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.authorBooks ab LEFT JOIN FETCH ab.book WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long id);
}