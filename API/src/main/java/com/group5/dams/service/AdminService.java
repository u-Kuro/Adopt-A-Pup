package com.group5.dams.service;

import com.group5.dams.model.Admin;
import com.group5.dams.repository.AdminRepository;
import com.group5.dams.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PersonRepository personRepository;

    public Admin addAdmin(Admin admin) {
        if (adminRepository.findByUserId(admin.getUserId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already an admin.");
        } else if (!personRepository.existsById(admin.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not found.");
        }
        return adminRepository.save(admin);
    }

    public Admin deleteAdmin(long userId) {
        Optional<Admin> foundAdminOptional = adminRepository.findByUserId(userId);
        if (foundAdminOptional.isPresent()) {
            Admin foundAdmin = adminRepository.findById(foundAdminOptional.get().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person is not found."));
            adminRepository.delete(foundAdmin);
            return foundAdmin;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not an admin.");
        }
    }

    public boolean isAdmin(long userId) {
        return adminRepository.findByUserId(userId).isPresent();
    }
}
