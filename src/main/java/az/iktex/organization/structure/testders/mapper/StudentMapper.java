package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.entity.Student;
import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student toEntity(StudentRequest request);

    // Adi siyahılar üçün courses sahəsi ignore olunur
    @Mapping(target = "courses", ignore = true)
    StudentResponse toResponse(Student student);

    // Detail sorğu üçün courses doldurulur
    @Named("withCourses")
    @Mapping(source = "courses", target = "courses")
    StudentResponse toDetailResponse(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courses", ignore = true)
    void updateEntity(StudentRequest request, @MappingTarget Student student);

    List<StudentResponse> toResponseList(List<Student> students);
}
