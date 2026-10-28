package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.AuthorBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AuthorBookRepository extends JpaRepository<AuthorBook, Long> {

    boolean existsByAuthorIdAndBookId(Long authorId, Long bookId);

    Optional<AuthorBook> findByAuthorIdAndBookId(Long authorId, Long bookId);
}