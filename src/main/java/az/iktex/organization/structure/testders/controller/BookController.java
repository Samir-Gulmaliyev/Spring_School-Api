package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@RequestBody BookRequest request) {
        return bookService.createBook(request);
    }

    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping
    public List<BookResponse> getAllBooks(@RequestParam(required = false) String title) {
        return bookService.getAllBooks(title);
    }

    @GetMapping("/cheaper-than")
    public List<BookResponse> getBooksCheaperThan(@RequestParam BigDecimal price) {
        return bookService.getBooksCheaperThan(price);
    }

    @PutMapping("/{id}")
    public BookResponse updateBook(@PathVariable Long id, @RequestBody BookRequest request) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @PostMapping("/{bookId}/authors/{authorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addAuthorToBook(@PathVariable Long bookId, @PathVariable Long authorId) {
        bookService.addAuthorToBook(bookId, authorId);
    }

    @DeleteMapping("/{bookId}/authors/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAuthorFromBook(@PathVariable Long bookId, @PathVariable Long authorId) {
        bookService.removeAuthorFromBook(bookId, authorId);
    }
}