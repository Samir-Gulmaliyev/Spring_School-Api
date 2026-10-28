package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;
import az.iktex.organization.structure.testders.response.DepartmentWithEmployeesResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentRequest request);
    DepartmentResponse toResponse(Department department);
    DepartmentWithEmployeesResponse toWithEmployeesResponse(Department department);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntity(DepartmentRequest request, @MappingTarget Department department);

    List<DepartmentResponse> toResponseList(List<Department> departments);
}
