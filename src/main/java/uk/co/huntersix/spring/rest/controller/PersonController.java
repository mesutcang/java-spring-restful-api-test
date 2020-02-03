package uk.co.huntersix.spring.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import uk.co.huntersix.spring.rest.dto.PersonDto;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/person/{lastName}/{firstName}")
    public ResponseEntity<?> person(@PathVariable(value = "lastName") String lastName,
                                    @PathVariable(value = "firstName") String firstName) {
        Person person = personDataService.findPerson(lastName, firstName);
        if (person == null) {
            return new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(person, null, HttpStatus.OK);
    }

    @GetMapping("/person/{lastName}")
    public ResponseEntity<?> peopleWithSurname(@PathVariable(value = "lastName") String lastName) {
        List<Person> people = personDataService.findPeopleUsingSurname(lastName);
        if (people.size() == 0) {
            return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(Collections.singletonMap("people", people), null, HttpStatus.OK);
    }

    @PostMapping("/person")
    public ResponseEntity<?> createPerson(@RequestBody PersonDto personDto){
        if (StringUtils.isEmpty(personDto.getFirstName()) || StringUtils.isEmpty(personDto.getLastName())){
            return new ResponseEntity<>(null, null, HttpStatus.BAD_REQUEST);
        }
        Person person = personDataService.findPerson(personDto.getLastName(), personDto.getFirstName());

        if (person != null){
            return new ResponseEntity<>(null, null, HttpStatus.CONFLICT);
        }
        personDataService.addPerson(personDto);
        return ResponseEntity.created(URI.create("/person/" + personDto.getLastName() + "/" + personDto.getFirstName())).build();
    }

}