package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Department;
import az.iktex.organization.structure.testders.entity.Employee;
import az.iktex.organization.structure.testders.mapper.EmployeeMapper;
import az.iktex.organization.structure.testders.repository.DepartmentRepository;
import az.iktex.organization.structure.testders.repository.EmployeeRepository;
import az.iktex.organization.structure.testders.request.EmployeeRequest;
import az.iktex.organization.structure.testders.response.EmployeeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Departament tapılmadı: ID = " + request.departmentId()));

        // Biznes qaydası: Eyni departamentdə eyni ad-soyadlı işçi təkrar yaradıla bilməz
        if (employeeRepository.existsByDepartmentIdAndNameAndSurname(request.departmentId(), request.name(), request.surname())) {
            throw new IllegalStateException("Bu departamentdə bu ad və soyadla işçi artıq mövcuddur!");
        }

        Employee employee = employeeMapper.toEntity(request);
        department.addEmployee(employee); // Helper metod iki tərəfli əlaqəni qurur

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("İşçi tapılmadı: ID = " + id));
        return employeeMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartmentId(Long departmentId) {
        return employeeMapper.toResponseList(employeeRepository.findAllByDepartmentId(departmentId));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesBySalaryRange(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min dəyəri max dəyərindən böyük ola bilməz!");
        }
        return employeeMapper.toResponseList(employeeRepository.findAllBySalaryBetween(min, max));
    }

    @Transactional(readOnly = true)
    public Double getAverageSalaryByDepartment(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Departament tapılmadı: ID = " + departmentId);
        }
        return employeeRepository.getAverageSalaryByDepartmentId(departmentId);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("İşçi tapılmadı: ID = " + id));

        Department targetDepartment = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Departament tapılmadı: ID = " + request.departmentId()));

        // Unikal ad/soyad yoxlaması (başqa işçi nəzərə alınmaqla)
        if (employeeRepository.existsByDepartmentIdAndNameAndSurnameAndIdNot(
                request.departmentId(), request.name(), request.surname(), id)) {
            throw new IllegalStateException("Bu departamentdə eyni ad və soyadlı başqa işçi mövcuddur!");
        }

        // Əgər departament dəyişibsə, köhnə əlaqəni kəsib yeni departamentə əlavə edirik
        if (!employee.getDepartment().getId().equals(targetDepartment.getId())) {
            employee.getDepartment().removeEmployee(employee);
            targetDepartment.addEmployee(employee);
        }

        employeeMapper.updateEntity(request, employee);
        return employeeMapper.toResponse(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("İşçi tapılmadı: ID = " + id));

        // Departament qalmalıdır, işçinin əlaqəsini kəsib silirik
        employee.getDepartment().removeEmployee(employee);
        employeeRepository.delete(employee);
    }
}