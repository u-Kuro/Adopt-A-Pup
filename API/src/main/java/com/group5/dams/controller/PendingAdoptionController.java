package com.group5.dams.controller;

import com.group5.dams.model.PendingAdoption;
import com.group5.dams.service.PendingAdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class PendingAdoptionController {
    @Autowired
    private PendingAdoptionService pendingAdoptionService;

    @RequestMapping("/api/pending-adoptions")
    public List<PendingAdoption> showPendingAdoptions(){ return pendingAdoptionService.getPendingAdoptions(); }

    @RequestMapping("/api/user-pending-adoptions/{userid}")
    public List<PendingAdoption> showUserPendingAdoptions(long userid){ return pendingAdoptionService.getUserPendingAdoptions(userid); }

    @RequestMapping(value = "/api/pending-adoption/{id}")
    public PendingAdoption showPendingAdoption(@PathVariable long id) {
        return pendingAdoptionService.getPendingAdoption(id);
    }

    @RequestMapping(value="/api/add-pending-adoption", method= RequestMethod.POST)
    public PendingAdoption addPendingAdoption(@RequestBody PendingAdoption pendingAdoption) { return pendingAdoptionService.addPendingAdoption(pendingAdoption); }

    @DeleteMapping(value="/api/delete-pending-adoption/{id}")
    public PendingAdoption deletePendingAdoption(@PathVariable long id) {
        return pendingAdoptionService.deletePendingAdoption(id);
    }
}
