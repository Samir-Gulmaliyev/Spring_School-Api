package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.entity.Student;
import az.iktex.organization.structure.testders.mapper.StudentMapper;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import az.iktex.organization.structure.testders.repository.StudentRepository;
import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        Student student = studentMapper.toEntity(request);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentWithCourses(Long id) {
        Student student = studentRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new EntityNotFoundException("Tələbə tapılmadı: ID = " + id));
        return studentMapper.toDetailResponse(student);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentMapper.toResponseList(studentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsWithoutCourses() {
        return studentMapper.toResponseList(studentRepository.findStudentsWithoutCourses());
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tələbə tapılmadı: ID = " + id));
        studentMapper.updateEntity(request, student);
        return studentMapper.toResponse(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tələbə tapılmadı: ID = " + id));
        studentRepository.delete(student);
    }

    @Transactional
    public void enrollCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findByIdWithCourses(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Tələbə tapılmadı: ID = " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Kurs tapılmadı: ID = " + courseId));

        // Biznes qaydası: Tələbə eyni kursa iki dəfə yazıla bilməz
        if (studentRepository.existsByIdAndCoursesId(studentId, courseId)) {
            throw new IllegalStateException("Tələbə artıq bu kursa yazılıb!");
        }

        student.addCourse(course);
    }

    @Transactional
    public void unenrollCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findByIdWithCourses(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Tələbə tapılmadı: ID = " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Kurs tapılmadı: ID = " + courseId));

        // Biznes qaydası: Yazılmadığı kursdan çıxarmaq xəta verməlidir
        boolean isEnrolled = student.getCourses().stream().anyMatch(c -> c.getId().equals(courseId));
        if (!isEnrolled) {
            throw new IllegalStateException("Tələbə bu kursa yazılmayıb ki, çıxarılsın!");
        }

        student.removeCourse(course);
    }
}