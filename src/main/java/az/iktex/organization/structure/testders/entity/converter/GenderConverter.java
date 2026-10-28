package az.iktex.organization.structure.testders.entity.converter;

import az.iktex.organization.structure.testders.enums.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class GenderConverter implements AttributeConverter<Gender, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Gender attribute) {
        return attribute != null ? attribute.getDbCode() : null;
    }

    @Override
    public Gender convertToEntityAttribute(Integer dbCode) {
        return dbCode != null ? Gender.findByDbCode(dbCode) : null;
    }
}
