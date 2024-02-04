package com.group5.dams.controller;

import com.group5.dams.model.Dog;
import com.group5.dams.service.DogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class DogController {
    @Autowired
    private DogService dogService;

    @RequestMapping(value = "/api/is-dog-available/{id}")
    public boolean isDogAvailable(@PathVariable long id) {
        return dogService.isDogAvailable(id);
    }

    @RequestMapping("/api/available-dogs")
    public List<Dog> showAvailableDogs(){ return dogService.getAvailableDogs(); }

    @RequestMapping("/api/dogs")
    public List<Dog> showDogs(){ return dogService.getDogs(); }

    @RequestMapping(value = "/api/dog/{id}")
    public Dog showDog(@PathVariable long id) {
        return dogService.getDog(id);
    }

    @RequestMapping(value="/api/add-dog", method=RequestMethod.POST)
    public Dog addDog(@RequestBody Dog dog) { return dogService.addDog(dog); }

    @PutMapping(value="/api/update-dog/{id}")
    public Dog updateDog(@PathVariable(value = "id") long id, @RequestBody Dog dog) {
        return dogService.updateDog(id, dog);
    }

    @DeleteMapping(value="/api/delete-dog/{id}")
    public Dog deleteDog(@PathVariable long id) {
        return dogService.deleteDog(id);
    }
}
