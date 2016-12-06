package com.romcharm.controllers;

import com.romcharm.defaults.APIErrorCode;
import com.romcharm.domain.Family;
import com.romcharm.exceptions.NotFoundException;
import com.romcharm.repositories.FamiliesRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/families")
public class FamiliesController {
    private final FamiliesRespository familiesRespository;

    @Autowired
    public FamiliesController(FamiliesRespository respository) {
        familiesRespository = respository;
    }

    @RequestMapping(value = "/{familyName}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public Family getFamily(@PathVariable("familyName") String familyName) {
        Family family = familiesRespository.findOne(familyName);
        if(family == null)
            throw new NotFoundException(APIErrorCode.FAMILY_NAME_NOT_FOUND);
        return family;
    }

    @RequestMapping(value = "/family", method = RequestMethod.PUT, consumes = {"application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveFamily(@RequestBody @Valid Family family) {
        familiesRespository.save(family);
    }
}
