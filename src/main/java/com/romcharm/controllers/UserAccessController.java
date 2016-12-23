package com.romcharm.controllers;

import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.UserRole;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.UserAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(
        allowedHeaders = "*",
        methods = {RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.GET})
@RequestMapping("/users")
public class UserAccessController {
    private final UserAccessRepository userAccessRepository;

    @Autowired
    public UserAccessController(UserAccessRepository userAccessRepository) {
        this.userAccessRepository = userAccessRepository;
    }

    @RequestMapping(value = {"/{username}"}, method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public UserRole getUserAccess(@PathVariable("username") String userName) {
        UserRole userRole = userAccessRepository.findOne(userName);
        if(userRole == null) {
            throw new NotFoundException(APIErrorCode.USER_NOT_FOUND);
        }
        return userRole;
    }

    @RequestMapping(value = {"/add"}, method = RequestMethod.PUT, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public void saveUserAccess(@RequestBody @Valid UserRole userRole) {
        UserRole access = userAccessRepository.findOne(userRole.getUserAccessName());
        if(access != null) {
            throw new IllegalArgumentException("User name already exists");
        }
        userAccessRepository.save(userRole);
    }
}
