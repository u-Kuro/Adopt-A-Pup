package com.group5.dams.controller;

import com.group5.dams.model.Person;
import com.group5.dams.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class PersonController {
    @Autowired
    private PersonService personService;

    @RequestMapping("/api/people")
    public List<Person> showPeople(){ return personService.getPeople(); }

    @RequestMapping(value = "/api/person/{id}")
    public Person showPerson(@PathVariable long id) {
        return personService.getPerson(id);
    }

    @RequestMapping(value="/api/add-person", method= RequestMethod.POST)
    public Person addPerson(@RequestBody Person person) { return personService.addPerson(person); }

    @PutMapping(value="/api/update-person/{id}")
    public Person updatePerson(@PathVariable(value = "id") long id, @RequestBody Person person) {
        return personService.updatePerson(id, person);
    }

    @DeleteMapping(value="/api/delete-person/{id}")
    public Person deletePerson(@PathVariable long id) {
        return personService.deletePerson(id);
    }
}
