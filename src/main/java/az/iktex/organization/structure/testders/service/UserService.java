package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.Passport;
import az.iktex.organization.structure.testders.entity.User;
import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.mapper.PassportMapper;
import az.iktex.organization.structure.testders.mapper.UserMapper;
import az.iktex.organization.structure.testders.repository.UserRepository;
import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.response.PassportResponse;
import az.iktex.organization.structure.testders.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PassportMapper passportMapper;

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        // İlk öncə email-i yoxlayırıq. Mövcuddursa xəta atırıq.
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new IllegalStateException("Bu email ilə artıq istifadəçi mövcuddur: " + userRequest.email());
        }

        //UserRequest map edirəm Entity-yə.
        User user = userMapper.toEntity(userRequest);

        // Əgər request-də passport gəlibsə, helper metod vasitəsilə iki tərəfli əlaqəni sinxronlaşdırıram.
        if (user.getPassport() != null) {
            user.setPassport(user.getPassport());
        }

        // Burda save metodu geriyə save olunan useri döndərir.
        User savedUser = userRepository.save(user);

        //Geriyə dönüş UserResponce olduğuna görə birbaşa return-nun içində map edib göndərirəm.
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdWithPassport(id)
                .orElseThrow(() -> new EntityNotFoundException("User tapılmadı: ID = " + id));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new EntityNotFoundException("User tapılmadı: Email = " + email));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(Gender gender) {
        List<User> users;
        if (gender != null) {
            users = userRepository.findAllByGender(gender);
        } else {
            users = userRepository.findAll();
        }
        return userMapper.toResponseList(users);

    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsersWithoutPassport() {
        List<User> users = userRepository.findAllByPassportIsNull();
        return userMapper.toResponseList(users);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        // 1. İstifadəçinin mövcud olub-olmadığını yoxlayırıq
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User tapılmadı: ID = " + id));
        // 2. Email başqa istifadəçi tərəfindən istifadə olunursa xəta atırıq
        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new IllegalStateException("Bu email başqa istifadəçiyə məxsusdur: " + request.email());
        }
        // 3. Mövcud entity-ni yeniləyirik.
        userMapper.updateEntity(request, existingUser);

        // 4. Dirty checking sayəsində save() çağırmağa ehtiyac yoxdur, tranzaksiya bitəndə yaddaşa yazılacaq
        return userMapper.toResponse(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        // 1. İstifadəçinin bazada olub-olmadığını yoxlayıram, yoxdursa EntityNotFoundException atıram
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Silinəcək User tapılmadı: ID = " + id);
        }

        // 2. Silirəm (Cascade.ALL və orphanRemoval = true sayəsində Passport da avtomatik silinəcək)
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse addPassportToUser(Long id, PassportRequest passportRequest) {
        // İstifadəçini passport ilə birlikdə tapırıq
        User user = userRepository.findByIdWithPassport(id)
                .orElseThrow(() -> new EntityNotFoundException("User tapılmadı: ID = " + id));

        // Bir user-in artıq passportu varsa, ikinci dəfə əlavə etmək xəta verməlidir
        if (user.getPassport() != null) {
            throw new IllegalStateException("Bu istifadəçinin artıq passportu mövcuddur!");
        }

        // Request-i Passport entity-sinə çeviririk
        Passport passport = passportMapper.toEntity(passportRequest);

        // Helper metod vasitəsilə əlaqəni sinxron qururuq
        user.setPassport(passport);

        // Tranzaksiya bitəndə dirty checking sayəsində avtomatik bazaya yenilənəcək
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public PassportResponse getUserPassport(Long id) {
        // İstifadəçini passport ilə birlikdə tapıram
        User user = userRepository.findByIdWithPassport(id)
                .orElseThrow(() -> new EntityNotFoundException("User tapılmadı: ID = " + id));

        // İstifadəçinin passportu yoxdursa, xəta atıram
        if (user.getPassport() == null) {
            throw new EntityNotFoundException("Bu istifadəçinin passportu mövcud deyil: ID = " + id);
        }

        // Passport entity-sini PassportResponse DTO-ya çevirib qaytarıram
        return passportMapper.toResponse(user.getPassport());
    }

    @Transactional
    public void deleteUserPassport(Long id) {
        // İstifadəçini passport ilə birlikdə tapıram
        User user = userRepository.findByIdWithPassport(id)
                .orElseThrow(() -> new EntityNotFoundException("User tapılmadı: ID = " + id));

        // İstifadəçinin onsuz da passportu yoxdursa xəta ata bilərik
        if (user.getPassport() == null) {
            throw new EntityNotFoundException("Bu istifadəçinin passportu mövcud deyil: ID = " + id);
        }

        // 3. Əlaqəni kəsirik. orphanRemoval = true olduğu üçün Passport bazadan avtomatik silinəcək
        user.setPassport(null);
    }
}
