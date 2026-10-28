package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.entity.Book;
import az.iktex.organization.structure.testders.request.BookRequest;
import az.iktex.organization.structure.testders.response.BookResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookRequest request);

    @Mapping(target = "authors", ignore = true)
    BookResponse toResponse(Book book);

    @Named("withAuthors")
    @Mapping(target = "authors", expression = "java(book.getAuthorBooks().stream().map(ab -> authorMapper.toResponse(ab.getAuthor())).toList())")
    BookResponse toDetailResponse(Book book, @Context AuthorMapper authorMapper);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    void updateEntity(BookRequest request, @MappingTarget Book book);

    List<BookResponse> toResponseList(List<Book> books);
}