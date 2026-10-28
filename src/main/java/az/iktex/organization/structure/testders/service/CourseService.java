package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Course;
import az.iktex.organization.structure.testders.entity.Student;
import az.iktex.organization.structure.testders.mapper.CourseMapper;
import az.iktex.organization.structure.testders.mapper.StudentMapper;
import az.iktex.organization.structure.testders.repository.CourseRepository;
import az.iktex.organization.structure.testders.repository.StudentRepository;
import az.iktex.organization.structure.testders.request.CourseRequest;
import az.iktex.organization.structure.testders.response.CourseResponse;
import az.iktex.organization.structure.testders.response.StudentResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByTitle(request.title())) {
            throw new IllegalStateException("Bu adda kurs artıq mövcuddur: " + request.title());
        }
        Course course = courseMapper.toEntity(request);
        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseMapper.toResponseList(courseRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kurs tapılmadı: ID = " + id));
        return courseMapper.toResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new EntityNotFoundException("Tələbə tapılmadı: ID = " + studentId);
        }
        return courseMapper.toResponseList(courseRepository.findCoursesByStudentId(studentId));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByCourseId(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new EntityNotFoundException("Kurs tapılmadı: ID = " + courseId);
        }
        List<Student> students = courseRepository.findStudentsByCourseId(courseId);
        return studentMapper.toResponseList(students);
    }

    @Transactional(readOnly = true)
    public Long countStudentsByCourseId(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new EntityNotFoundException("Kurs tapılmadı: ID = " + courseId);
        }
        return courseRepository.countStudentsByCourseId(courseId);
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kurs tapılmadı: ID = " + id));

        if (courseRepository.existsByTitleAndIdNot(request.title(), id)) {
            throw new IllegalStateException("Bu ad başqa kurs tərəfindən istifadə olunur: " + request.title());
        }

        courseMapper.updateEntity(request, course);
        return courseMapper.toResponse(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new EntityNotFoundException("Kurs tapılmadı: ID = " + id));

        /*
         * Kurs silinərkən əvvəlcə tələbələrlə əlaqəni qırmaq niyə lazımdır?
         * Çünki Course tərəfi ManyToMany əlaqəsində inverse (əks) tərəfdir (`mappedBy`).
         * Xarici açar (FK) məhdudiyyətinə və join cədvəlindəki qeydlərə görə əgər birbaşa
         * kursu silməyə çalışsaq, `student_course` cədvəlindəki bağlı sətirlərə görə DB tərəfdən
         * xəta (Foreign Key violation) alacağıq.
         * Buna görə də silməzdən əvvəl əlaqəni tərəflərdən qıraraq (removeCourse)
         * `student_course` cədvəlindəki uyğun sətirlərin təmizlənməsini təmin edirik.
         */
        for (Student student : new ArrayList<>(course.getStudents())) {
            student.removeCourse(course);
        }

        courseRepository.delete(course);
    }
}