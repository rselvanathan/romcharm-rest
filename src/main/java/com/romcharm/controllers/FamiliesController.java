package com.romcharm.controllers;

import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.Family;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.notification.NotificationService;
import com.romcharm.notification.domain.EmailMessage;
import com.romcharm.repositories.FamiliesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/families")
public class FamiliesController {
    private final FamiliesRepository familiesRespository;
    private final NotificationService notificationService;

    @Autowired
    public FamiliesController(FamiliesRepository repository, NotificationService service) {
        familiesRespository = repository;
        notificationService = service;
    }

    @RequestMapping(value = "/{email:.+}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public Family getFamily(@PathVariable("email") String email) {
        Family family = familiesRespository.findOne(email);
        if(family == null)
            throw new NotFoundException(APIErrorCode.EMAIL_NOT_FOUND);
        return family;
    }

    @RequestMapping(value = "/family", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public Family saveFamily(@RequestBody @Valid Family family) {
        Family familyResult = familiesRespository.findOne(family.getEmail());
        if(familyResult == null) {
            Family savedFamily = familiesRespository.save(family);
            EmailMessage message = getEmailMessage(savedFamily);
            // Blocking call currently
            notificationService.sendEmailNotificiation(message).join();
            return savedFamily;
        } else {
            throw new IllegalArgumentException(APIErrorCode.FAMILY_EXISTS.getReason());
        }
    }

    private EmailMessage getEmailMessage(Family family) {
        return new EmailMessage(family.getEmail(), family.getFirstName(), family.getLastName(), family.getAreAttending(), family.getNumberAttending());
    }
}
