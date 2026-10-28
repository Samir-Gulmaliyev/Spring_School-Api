package az.iktex.organization.structure.testders.response;

import java.util.List;

public record DepartmentWithEmployeesResponse(Long id, String name, String location, String phone, List<EmployeeResponse> employees) {
}
