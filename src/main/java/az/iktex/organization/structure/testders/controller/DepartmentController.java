package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;
import az.iktex.organization.structure.testders.response.DepartmentWithEmployeesResponse;
import az.iktex.organization.structure.testders.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // POST üçün 201 Created statusu
    public DepartmentResponse createDepartment(@RequestBody DepartmentRequest request) {
        return departmentService.createDepartment(request);
    }

    @GetMapping("/{id}")
    public DepartmentWithEmployeesResponse getDepartmentWithEmployees(@PathVariable Long id) {
        return departmentService.getDepartmentWithEmployees(id);
    }

    @GetMapping
    public List<DepartmentResponse> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/without-employees")
    public List<DepartmentResponse> getDepartmentsWithoutEmployees() {
        return departmentService.getDepartmentsWithoutEmployees();
    }

    @PutMapping("/{id}")
    public DepartmentResponse updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest request) {
        return departmentService.updateDepartment(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // DELETE üçün 204 No Content statusu
    public void deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
    }
}