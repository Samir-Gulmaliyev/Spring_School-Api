package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.entity.AuthorBook;
import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.mapper.AuthorMapper;
import az.iktex.organization.structure.testders.mapper.BookMapper;
import az.iktex.organization.structure.testders.repository.AuthorBookRepository;
import az.iktex.organization.structure.testders.repository.AuthorRepository;
import az.iktex.organization.structure.testders.repository.BookRepository;
import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final AuthorBookRepository authorBookRepository;
    private final BookMapper bookMapper;
    private final AuthorMapper authorMapper;

    @Transactional
    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsByTitle(request.title())) {
            throw new IllegalStateException("Bu adda kitab artıq mövcuddur: " + request.title());
        }
        Book book = bookMapper.toEntity(request);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findByIdWithAuthors(id)
                .orElseThrow(() -> new EntityNotFoundException("Kitab tapılmadı: ID = " + id));
        return bookMapper.toDetailResponse(book, authorMapper);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks(String title) {
        List<Book> books = (title != null && !title.isBlank())
                ? bookRepository.findAllByTitleContainingIgnoreCase(title)
                : bookRepository.findAll();
        return bookMapper.toResponseList(books);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksCheaperThan(BigDecimal price) {
        return bookMapper.toResponseList(bookRepository.findAllByPriceLessThanEqualOrderByPriceAsc(price));
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kitab tapılmadı: ID = " + id));

        if (bookRepository.existsByTitleAndIdNot(request.title(), id)) {
            throw new IllegalStateException("Bu başlıqlı başqa kitab mövcuddur: " + request.title());
        }

        bookMapper.updateEntity(request, book);
        return bookMapper.toResponse(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kitab tapılmadı: ID = " + id));
        bookRepository.delete(book);
    }

    @Transactional
    public void addAuthorToBook(Long bookId, Long authorId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Kitab tapılmadı: ID = " + bookId));
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Müəllif tapılmadı: ID = " + authorId));

        if (authorBookRepository.existsByAuthorIdAndBookId(authorId, bookId)) {
            throw new IllegalStateException("Bu müəllif artıq həmin kitaba bağlanıb!");
        }

        author.addBook(book);
    }

    @Transactional
    public void removeAuthorFromBook(Long bookId, Long authorId) {
        AuthorBook authorBook = authorBookRepository.findByAuthorIdAndBookId(authorId, bookId)
                .orElseThrow(() -> new EntityNotFoundException("Belə bir əlaqə tapılmadı!"));

        authorBook.getAuthor().removeBook(authorBook.getBook());
    }
}