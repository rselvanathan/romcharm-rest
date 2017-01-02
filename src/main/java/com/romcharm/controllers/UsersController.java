package com.romcharm.controllers;

import com.romcharm.authorization.JWTUtil;
import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.Login;
import com.romcharm.domain.Token;
import com.romcharm.domain.User;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.romcharm.defaults.APIErrorCode.PASSWORD_INCORRECT;
import static com.romcharm.defaults.APIErrorCode.USER_EXISTS;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    public UsersController(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public Token getUser(@RequestBody @Valid Login login) {
        User user = userRepository.findOne(login.getUsername());
        if(user == null) {
            throw new NotFoundException(APIErrorCode.USER_NOT_FOUND);
        }
        if(!user.getPassword().equals(login.getPassword())) {
            throw new NotFoundException(PASSWORD_INCORRECT);
        }
        return Token.builder().token(jwtUtil.generateToken(user)).build();
    }


    @RequestMapping(value = {"/add"}, method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public User saveUser(@RequestBody @Valid User userRole) {
        User user = userRepository.findOne(userRole.getUsername());
        if(user != null) {
            throw new IllegalArgumentException(USER_EXISTS.getReason());
        }
        return userRepository.save(userRole);
    }
}
