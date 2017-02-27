package com.romcharm.controllers;

import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.romcharm.Family;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.notification.NotificationService;
import com.romcharm.repositories.Repository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/families")
public class FamiliesController {
    private Logger logger = LoggerFactory.getLogger(FamiliesController.class);

    private final Repository<Family> familiesRepository;
    private final NotificationService notificationService;

    @Autowired
    public FamiliesController(Repository<Family> repository, NotificationService service) {
        familiesRepository = repository;
        notificationService = service;
    }

    @RequestMapping(value = "/{email:.+}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = Family.class),
        @ApiResponse(code = 404, message = "E-mail not found"),
    })
    public Family getFamily(@PathVariable("email") String email) {
        Family family = familiesRepository.findOne(email);
        if(family == null)
            throw new NotFoundException(APIErrorCode.EMAIL_NOT_FOUND);
        return family;
    }

    @RequestMapping(value = "/family", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = Family.class),
        @ApiResponse(code = 400, message = "Bad Request - Invalid or Bad Data"),
    })
    public Family saveFamily(@RequestBody @Valid Family family) {
        Family familyResult = familiesRepository.findOne(family.getEmail());
        if(familyResult == null) {
            Family savedFamily = familiesRepository.save(family);
            notificationService.sendEmailNotification(savedFamily);
            return savedFamily;
        } else {
            throw new IllegalArgumentException(APIErrorCode.FAMILY_EXISTS.getReason());
        }
    }
}
