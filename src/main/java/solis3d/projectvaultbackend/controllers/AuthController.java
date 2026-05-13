package solis3d.projectvaultbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.exceptions.ValidationException;
import solis3d.projectvaultbackend.payloads.LoginDTO;
import solis3d.projectvaultbackend.payloads.LoginRespDTO;
import solis3d.projectvaultbackend.payloads.NewUserRespDTO;
import solis3d.projectvaultbackend.payloads.RegisterDTO;
import solis3d.projectvaultbackend.services.AppUsersService;
import solis3d.projectvaultbackend.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AppUsersService appUsersService;

    public AuthController(AuthService authService, AppUsersService appUsersService) {
        this.authService = authService;
        this.appUsersService = appUsersService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserRespDTO register(@RequestBody @Validated RegisterDTO body, BindingResult validationResult) {
        if(validationResult.hasErrors()){
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }
        AppUser newUser = this.appUsersService.saveNewUser(body);
        return new NewUserRespDTO(newUser.getId());
    }


    @PostMapping("/login")
    public LoginRespDTO login(@RequestBody @Validated LoginDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }

        return new LoginRespDTO(this.authService.checkCredentialsAndGenerateToken(body));
    }
}
