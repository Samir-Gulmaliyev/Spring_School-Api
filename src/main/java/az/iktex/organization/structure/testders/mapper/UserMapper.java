package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.entity.User;
import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PassportMapper.class})
public interface UserMapper {
    User toEntity(UserRequest request);

    UserResponse toResponse(User entity);

    List<UserResponse> toResponseList(List<User> entityList);

    void updateEntity(UserRequest request, @MappingTarget User entity);
}
