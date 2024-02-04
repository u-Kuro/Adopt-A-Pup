package com.group5.dams.service;

import com.group5.dams.model.PendingAdoption;
import com.group5.dams.repository.DogRepository;
import com.group5.dams.repository.PendingAdoptionRepository;
import com.group5.dams.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PendingAdoptionService {
    @Autowired
    private PendingAdoptionRepository pendingAdoptionRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private DogRepository dogRepository;

    public List<PendingAdoption> getPendingAdoptions() {
        return (List<PendingAdoption>) pendingAdoptionRepository.findAll();
    }

    public List<PendingAdoption> getUserPendingAdoptions(long userId) {
        Optional<List<PendingAdoption>> userPendingAdoptions = pendingAdoptionRepository.findByUserId(userId);
        if (userPendingAdoptions.isEmpty()) {
            return userPendingAdoptions.get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User pending adoptions are not found.");
        }
    }

    public PendingAdoption getPendingAdoption(long id) {
        return pendingAdoptionRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pending adoption is not found."));
    }

    public PendingAdoption addPendingAdoption(PendingAdoption pendingAdoption) {
        if (!personRepository.existsById(pendingAdoption.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not found.");
        }
        if (!dogRepository.existsById(pendingAdoption.getDogId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dog is not found.");
        }
        if(pendingAdoptionRepository.findByDogId(pendingAdoption.getDogId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dog is already pending to be adopted.");
        }
        Optional<List<PendingAdoption>> foundOptionalPendingAdoptions = pendingAdoptionRepository.findByUserId(pendingAdoption.getUserId());
        if(foundOptionalPendingAdoptions.isPresent()) {
            List<PendingAdoption> pendingAdoptions = foundOptionalPendingAdoptions.get();
            for (PendingAdoption foundPendingAdoption: pendingAdoptions) {
                if (foundPendingAdoption.getDogId()==pendingAdoption.getDogId()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Adoption is already pending.");
                }
            }
        }
        pendingAdoption.setPendingAdoptionDateTime(LocalDateTime.now());
        return pendingAdoptionRepository.save(pendingAdoption);
    }

    public PendingAdoption deletePendingAdoption(long id) {
        PendingAdoption pendingAdoption = getPendingAdoption(id);
        pendingAdoptionRepository.delete(pendingAdoption);
        return pendingAdoption;
    }
}
