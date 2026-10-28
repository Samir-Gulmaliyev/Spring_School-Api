package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.entity.Passport;
import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.response.PassportResponse;
import az.iktex.organization.structure.testders.response.UserResponse;
import az.iktex.organization.structure.testders.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse save(@RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/by-email")
    public UserResponse getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }

    //Burda parametrdə required = false yazmağımın səbəbi yəni Gender yazmağ vacib deyil.Olada bilər olmayada.
    @GetMapping()
    public List<UserResponse> getAllUsers(@RequestParam(required = false) Gender gender) {
        return userService.getAllUsers(gender);
    }

    @GetMapping(path = "/without-passport")
    public List<UserResponse> getAllUsersWithoutPassport() {
        return userService.getAllUsersWithoutPassport();
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{id}/passport")
    public UserResponse addPassportToUser(@PathVariable Long id, @RequestBody PassportRequest passportRequest) {
        return userService.addPassportToUser(id, passportRequest);
    }

    @GetMapping("/{id}/passport")
    public PassportResponse getUserPassport(@PathVariable Long id) {
        return userService.getUserPassport(id);
    }

    @DeleteMapping("/{id}/passport")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserPassport(@PathVariable Long id) {
        userService.deleteUserPassport(id);
    }
}
