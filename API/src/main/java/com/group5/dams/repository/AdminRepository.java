package com.group5.dams.repository;


import com.group5.dams.model.Admin;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Long> {
    @Query("SELECT a FROM Admin a WHERE a.userId = :userId")
    Optional<Admin> findByUserId(@Param("userId") long userId);
}
