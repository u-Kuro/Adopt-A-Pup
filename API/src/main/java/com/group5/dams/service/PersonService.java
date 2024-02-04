package com.group5.dams.service;

import com.group5.dams.model.PendingAdoption;
import com.group5.dams.model.Person;
import com.group5.dams.repository.AdminRepository;
import com.group5.dams.repository.PendingAdoptionRepository;
import com.group5.dams.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PendingAdoptionRepository pendingAdoptionRepository;

    public List<Person> getPeople() {
    return (List<Person>) personRepository.findAll();
  }

    public Person getPerson(long id) {
        return personRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not found."));
    }

    public Person addPerson(Person person) {
        if (person.getFirstName()==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name is required.");
        }
        if (person.getLastName()==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Last name is required.");
        }
        if (person.getEmail()==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email name is required.");
        }
        if (person.getPassword()==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password name is required.");
        }
        return personRepository.save(person);
  }

    public Person updatePerson(long id, Person person) {
        Person foundPerson = getPerson(id);
        if (person.getFirstName()!=null) {
            foundPerson.setFirstName(person.getFirstName());
        }
        if (person.getLastName()!=null) {
          foundPerson.setLastName(person.getLastName());
        }
        if (person.getEmail()!=null) {
            foundPerson.setEmail(person.getEmail());
        }
        if (person.getPassword()!=null) {
            foundPerson.setPassword(person.getPassword());
        }
        return personRepository.save(foundPerson);
    }

    public Person deletePerson(long id) {
        Person person = getPerson(id);
        adminRepository.findByUserId(person.getId()).ifPresent(admin -> adminRepository.delete(admin));
        Optional<List<PendingAdoption>> foundOptionalPendingAdoptions = pendingAdoptionRepository.findByUserId(person.getId());
        if (foundOptionalPendingAdoptions.isPresent()) {
            List<PendingAdoption> pendingAdoptions = foundOptionalPendingAdoptions.get();
            for (PendingAdoption foundPendingAdoption : pendingAdoptions) {
                pendingAdoptionRepository.delete(foundPendingAdoption);
            }
        }
        personRepository.delete(person);
        return person;
    }
}
