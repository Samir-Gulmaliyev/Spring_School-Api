package az.iktex.organization.structure.testders.response;

import java.util.List;

public record AuthorResponse(Long id,
                             String name,
                             String surname,
                             String country,
                             List<BookResponse> books) {
}
