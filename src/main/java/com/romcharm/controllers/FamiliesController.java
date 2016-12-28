package com.romcharm.controllers;

import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.Family;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.FamiliesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/families")
public class FamiliesController {
    private final FamiliesRepository familiesRespository;

    @Autowired
    public FamiliesController(FamiliesRepository respository) {
        familiesRespository = respository;
    }

    @RequestMapping(value = "/{email:.+}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public Family getFamily(@PathVariable("email") String email) {
        Family family = familiesRespository.findOne(email);
        if(family == null)
            throw new NotFoundException(APIErrorCode.EMAIL_NOT_FOUND);
        return family;
    }

    @RequestMapping(value = "/family", method = RequestMethod.PUT, consumes = {"application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveFamily(@RequestBody @Valid Family family) {
        familiesRespository.save(family);
    }
}
