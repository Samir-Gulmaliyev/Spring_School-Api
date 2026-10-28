package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.entity.AuthorBook;
import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.mapper.AuthorMapper;
import az.iktex.organization.structure.testders.mapper.BookMapper;
import az.iktex.organization.structure.testders.repository.AuthorRepository;
import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;
import az.iktex.organization.structure.testders.response.BookResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        if (authorRepository.existsByNameAndSurname(request.name(), request.surname())) {
            throw new IllegalStateException("Bu ad və soyadla müəllif artıq mövcuddur!");
        }
        Author author = authorMapper.toEntity(request);
        return authorMapper.toResponse(authorRepository.save(author));
    }

    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findByIdWithBooks(id)
                .orElseThrow(() -> new EntityNotFoundException("Müəllif tapılmadı: ID = " + id));
        return authorMapper.toDetailResponse(author, bookMapper);
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> getAllAuthors(String country) {
        List<Author> authors = (country != null && !country.isBlank())
                ? authorRepository.findAllByCountry(country)
                : authorRepository.findAll();
        return authorMapper.toResponseList(authors);
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> getAuthorsWithoutBooks() {
        return authorMapper.toResponseList(authorRepository.findAuthorsWithoutBooks());
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByAuthorId(Long id) {
        Author author = authorRepository.findByIdWithBooks(id)
                .orElseThrow(() -> new EntityNotFoundException("Müəllif tapılmadı: ID = " + id));
        List<Book> books = author.getAuthorBooks().stream().map(AuthorBook::getBook).toList();
        return bookMapper.toResponseList(books);
    }

    @Transactional
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Müəllif tapılmadı: ID = " + id));

        if (authorRepository.existsByNameAndSurnameAndIdNot(request.name(), request.surname(), id)) {
            throw new IllegalStateException("Bu ad və soyad başqa müəllifə məxsusdur!");
        }

        authorMapper.updateEntity(request, author);
        return authorMapper.toResponse(author);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Müəllif tapılmadı: ID = " + id));
        // Müəllif silinəndə orphanRemoval sayəsində yalnız author_book əlaqələri silinir, kitablar qalır.
        authorRepository.delete(author);
    }
}