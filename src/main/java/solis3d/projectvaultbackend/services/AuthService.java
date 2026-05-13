package solis3d.projectvaultbackend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.exceptions.UnauthorizedException;
import solis3d.projectvaultbackend.payloads.LoginDTO;
import solis3d.projectvaultbackend.security.TokenTools;

@Service
public class AuthService {

    private final AppUsersService appUsersService;
    private final PasswordEncoder bcrypt;
    private final TokenTools tokenTools;

    public AuthService(AppUsersService appUsersService, PasswordEncoder bcrypt, TokenTools tokenTools) {
        this.appUsersService = appUsersService;
        this.bcrypt = bcrypt;
        this.tokenTools = tokenTools;
    }

    public String checkCredentialsAndGenerateToken(LoginDTO body) {
        AppUser foundUser = this.appUsersService.findByEmail(body.email());

        if(this.bcrypt.matches(body.password(), foundUser.getPassword())) {
            return this.tokenTools.generateToken(foundUser);
        } else {
            throw new UnauthorizedException("Credenziali non valide!");
        }
    }
}
