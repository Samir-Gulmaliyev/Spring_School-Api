package az.iktex.organization.structure.testders.repository;

import az.iktex.organization.structure.testders.entity.User;
import az.iktex.organization.structure.testders.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository //Qaydalara görə Repository anotasiyasın yazdım, yeni versiyada yazmasaqda qabul edir.
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    // JPQL ilə JOIN FETCH sorğusu (N+1 probleminin qarşısını alır və passport-u birgə gətirir)
    @Query("Select u From User u Left Join Fetch u.passport where u.id = :id")
    Optional<User> findByIdWithPassport(@Param("id") Long id);

    //Burda özüm Optional verdim ki User gələdə bilər gəlməyədə bilər.
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.passport WHERE u.email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);

    List<User> findAllByGender(Gender gender);

    List<User> findAllByPassportIsNull();

    Boolean existsByEmailAndIdNot(String email, Long id);

}
