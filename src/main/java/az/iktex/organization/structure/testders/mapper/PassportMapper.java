package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.entity.Passport;
import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.response.PassportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PassportMapper {
    Passport toEntity(PassportRequest request);

    PassportResponse toResponse(Passport entity);

    List<PassportResponse> toResponseList(List<Passport> entityList);

    void updateEntity(PassportRequest request, @MappingTarget Passport entity);
}
