package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;
import az.iktex.organization.structure.testders.response.BookResponse;
import az.iktex.organization.structure.testders.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse createAuthor(@RequestBody AuthorRequest request) {
        return authorService.createAuthor(request);
    }

    @GetMapping("/{id}")
    public AuthorResponse getAuthorById(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }

    @GetMapping
    public List<AuthorResponse> getAllAuthors(@RequestParam(required = false) String country) {
        return authorService.getAllAuthors(country);
    }

    @GetMapping("/without-books")
    public List<AuthorResponse> getAuthorsWithoutBooks() {
        return authorService.getAuthorsWithoutBooks();
    }

    @GetMapping("/{id}/books")
    public List<BookResponse> getBooksByAuthorId(@PathVariable Long id) {
        return authorService.getBooksByAuthorId(id);
    }

    @PutMapping("/{id}")
    public AuthorResponse updateAuthor(@PathVariable Long id, @RequestBody AuthorRequest request) {
        return authorService.updateAuthor(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
    }
}