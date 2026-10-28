package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.mapper.DepartmentMapper;
import az.iktex.organization.structure.testders.repository.DepartmentRepository;
import az.iktex.organization.structure.testders.request.DepartmentRequest;
import az.iktex.organization.structure.testders.response.DepartmentResponse;
import az.iktex.organization.structure.testders.response.DepartmentWithEmployeesResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.name())) {
            throw new IllegalStateException("Bu adla departament mövcuddur: " + request.name());
        }
        Department department = departmentMapper.toEntity(request);
        Department saved = departmentRepository.save(department);
        return departmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public DepartmentWithEmployeesResponse getDepartmentWithEmployees(Long id) {
        Department department = departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new EntityNotFoundException("Departament tapılmadı: ID = " + id));
        return departmentMapper.toWithEmployeesResponse(department);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentMapper.toResponseList(departmentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getDepartmentsWithoutEmployees() {
        return departmentMapper.toResponseList(departmentRepository.findDepartmentsWithoutEmployees());
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Departament tapılmadı: ID = " + id));

        if (departmentRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new IllegalStateException("Bu ad başqa departamentdə istifadə olunur: " + request.name());
        }

        departmentMapper.updateEntity(request, department);
        return departmentMapper.toResponse(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new EntityNotFoundException("Departament tapılmadı: ID = " + id));

        // Biznes qaydası: İşçisi varsa silmək OLMAZ
        if (!department.getEmployees().isEmpty()) {
            throw new IllegalStateException("İşçisi olan departament silinə bilməz!");
        }

        departmentRepository.delete(department);
    }
}
