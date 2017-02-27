package com.romcharm.controllers;

import com.romcharm.authorization.JWTUtil;
import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.Login;
import com.romcharm.domain.Token;
import com.romcharm.domain.User;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.Repository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.romcharm.defaults.APIErrorCode.PASSWORD_INCORRECT;
import static com.romcharm.defaults.APIErrorCode.USER_EXISTS;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final Repository<User> userRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    public UsersController(Repository<User> userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @RequestMapping(value = {"/auth"}, method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Token.class),
        @ApiResponse(code = 404, message = "User not found/Password incorrect"),
    })
    public Token getUser(@RequestBody @Valid Login login) {
        User user = userRepository.findOne(login.getUsername());
        if(user == null) {
            throw new NotFoundException(APIErrorCode.USER_NOT_FOUND);
        }
        if(!user.getPassword().equals(login.getPassword())) {
            throw new NotFoundException(PASSWORD_INCORRECT);
        }
        return new Token(jwtUtil.generateToken(user));
    }


    @ApiOperation(value = "Add a user", notes = "An admin only endpoint to add a user")
    @RequestMapping(value = {"/add"}, method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = User.class),
        @ApiResponse(code = 400, message = "Bad Request - Invalid Data or User already exists"),
    })
    public User saveUser(@RequestBody @Valid User userRole) {
        User user = userRepository.findOne(userRole.getUsername());
        if(user != null) {
            throw new IllegalArgumentException(USER_EXISTS.getReason());
        }
        return userRepository.save(userRole);
    }
}
