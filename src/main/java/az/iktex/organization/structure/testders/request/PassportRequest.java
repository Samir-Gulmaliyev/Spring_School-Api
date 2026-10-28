package az.iktex.organization.structure.testders.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record PassportRequest(
        String serialNumber,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate issueDate,
        String country) {
}
