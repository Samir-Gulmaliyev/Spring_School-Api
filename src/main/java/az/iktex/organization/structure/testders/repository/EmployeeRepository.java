package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Eyni departamentdə eyni ad-soyadlı işçi olub- olmadığını yoxlamaq üçün
    boolean existsByDepartmentIdAndNameAndSurname(Long departmentId, String name, String surname);

    boolean existsByDepartmentIdAndNameAndSurnameAndIdNot(Long departmentId, String name, String surname, Long id);

    // Departamentin işçiləri
    List<Employee> findAllByDepartmentId(Long departmentId);

    // Maaş aralığına görə
    List<Employee> findAllBySalaryBetween(BigDecimal min, BigDecimal max);

    // Departamentin orta maaşı (JPQL avg)
    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.department.id = :departmentId")
    Double getAverageSalaryByDepartmentId(@Param("departmentId") Long departmentId);
}
