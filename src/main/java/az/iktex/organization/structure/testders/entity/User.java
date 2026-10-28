package az.iktex.organization.structure.testders.entity;

import az.iktex.organization.structure.testders.entity.converter.GenderConverter;
import az.iktex.organization.structure.testders.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "surname", nullable = false, length = 50)
    private String surname;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "gender_id", nullable = false)
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Passport passport;

    public void setPassport(Passport passport){
        this.passport=passport;
        if (passport!=null){
            passport.setUser(this);
        }
    }
}
