package com.example.ecommerceuserservice.controllers;

import com.example.ecommerceuserservice.dtos.LogInRequestDTO;
import com.example.ecommerceuserservice.dtos.LogInResponseDTO;
import com.example.ecommerceuserservice.dtos.SignUpRequestDTO;
import com.example.ecommerceuserservice.dtos.SignUpResponseDTO;
import com.example.ecommerceuserservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("")
    public String health() {
        return "User service is running fine";
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        SignUpResponseDTO signUpResponseDTO = new SignUpResponseDTO();
        HttpStatus httpStatus;
        String email = signUpRequestDTO.getEmail();
        String password = signUpRequestDTO.getPassword();
        try {
            if (this.authService.signUp(email, password)) {
                signUpResponseDTO.setStatus("SUCCESS");
                httpStatus = HttpStatus.OK;
            } else {
                signUpResponseDTO.setStatus("FAILURE");
                httpStatus = HttpStatus.FORBIDDEN;
            }
        } catch (Exception e) {
            signUpResponseDTO.setStatus("FAILURE");
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity<>(signUpResponseDTO, null, httpStatus);
    }

    @PostMapping("/login")
    public ResponseEntity<LogInResponseDTO> logIn(@RequestBody LogInRequestDTO logInRequestDTO) {
        LogInResponseDTO logInResponseDTO = new LogInResponseDTO();
        HttpStatus httpStatus;
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String email = logInRequestDTO.getEmail();
        String password = logInRequestDTO.getPassword();
        try {
            String token = this.authService.logIn(email, password);
            logInResponseDTO.setStatus("SUCCESS");
            headers.add("AUTH_TOKEN", token);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            logInResponseDTO.setStatus("FAILURE");
            httpStatus = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<>(logInResponseDTO, headers, httpStatus);
    }

//    TODO
    @DeleteMapping("/logout")
    public void logOut(@RequestParam("token") String jwtToken) {
        System.out.println(jwtToken);
        this.authService.logOut(jwtToken);
    }

//    TODO
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam("token") String jwtToken) {
        System.out.println(jwtToken);
        Boolean response;
        HttpStatus httpStatus;
        try {
            this.authService.validateJwtToken(jwtToken);
            response = true;
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            response = false;
            httpStatus = HttpStatus.FORBIDDEN;
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @GetMapping("/fake_validate")
    public Boolean fakeValidate() {
        if (Math.random() > 0.5) {
            return true;
        }
        return false;
    }
}
