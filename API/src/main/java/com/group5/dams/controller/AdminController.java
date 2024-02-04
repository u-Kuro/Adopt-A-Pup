package com.group5.dams.controller;

import com.group5.dams.model.Admin;
import com.group5.dams.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class AdminController {
    @Autowired
    private AdminService adminService;

    @RequestMapping(value = "/api/is-admin/{userid}")
    public boolean isAdmin(@PathVariable long userid) {
        return adminService.isAdmin(userid);
    }

    @RequestMapping(value="/api/add-admin", method=RequestMethod.POST)
    public Admin addAdmin(@RequestBody Admin admin) { return adminService.addAdmin(admin); }

    @DeleteMapping(value="/api/delete-admin/{userid}")
    public Admin deleteAdmin(@PathVariable long userid) {
        return adminService.deleteAdmin(userid);
    }
}
