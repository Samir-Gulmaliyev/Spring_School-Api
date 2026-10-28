package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.entity.Author;
import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    Author toEntity(AuthorRequest request);

    @Mapping(target = "books", ignore = true)
    AuthorResponse toResponse(Author author);

    @Named("withBooks")
    @Mapping(target = "books", expression = "java(author.getAuthorBooks().stream().map(ab -> bookMapper.toResponse(ab.getBook())).toList())")
    AuthorResponse toDetailResponse(Author author, @Context BookMapper bookMapper);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorBooks", ignore = true)
    void updateEntity(AuthorRequest request, @MappingTarget Author author);

    List<AuthorResponse> toResponseList(List<Author> authors);
}