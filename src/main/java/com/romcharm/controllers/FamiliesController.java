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
@CrossOrigin(
        allowedHeaders = "*",
        methods = {RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.OPTIONS, RequestMethod.GET})
@RequestMapping("/families")
public class FamiliesController {
    private final FamiliesRespository familiesRespository;

    @Autowired
    public FamiliesController(FamiliesRespository respository) {
        familiesRespository = respository;
    }

    @RequestMapping(value = "/{rsvpName}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseBody
    public Family getFamily(@PathVariable("rsvpName") String rsvpName) {
        Family family = familiesRespository.findOne(rsvpName);
        if(family == null)
            throw new NotFoundException(APIErrorCode.FAMILY_NAME_NOT_FOUND);
        return family;
    }

    @RequestMapping(value = "/family", method = RequestMethod.PUT, consumes = {"application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveFamily(@RequestBody @Valid Family family) {
        familiesRespository.save(family);
    }

    /**
     * TODO MAKE THIS API PRIVATE - E.G Can only be called with a admin Authentication
     */
    @RequestMapping(value = "/{rsvpName}", method = RequestMethod.PUT, produces = {"application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void addRSVPEntry(@PathVariable String rsvpName) {
        Family family = Family.builder().rsvpName(rsvpName).registered(false).build();
        familiesRespository.save(family);
    }
}
