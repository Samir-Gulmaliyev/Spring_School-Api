package az.iktex.organization.structure.testders.controller;


import az.iktex.organization.structure.testders.request.StudentRequest;
import az.iktex.organization.structure.testders.response.StudentResponse;
import az.iktex.organization.structure.testders.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse createStudent(@RequestBody StudentRequest request) {
        return studentService.createStudent(request);
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentWithCourses(@PathVariable Long id) {
        return studentService.getStudentWithCourses(id);
    }

    @GetMapping
    public List<StudentResponse> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/without-courses")
    public List<StudentResponse> getStudentsWithoutCourses() {
        return studentService.getStudentsWithoutCourses();
    }

    @PutMapping("/{id}")
    public StudentResponse updateStudent(@PathVariable Long id, @RequestBody StudentRequest request) {
        return studentService.updateStudent(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void enrollCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.enrollCourse(studentId, courseId);
    }

    @DeleteMapping("/{studentId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenrollCourse(@PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.unenrollCourse(studentId, courseId);
    }
}