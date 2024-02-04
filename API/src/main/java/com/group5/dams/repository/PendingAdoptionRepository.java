package com.group5.dams.repository;

import com.group5.dams.model.PendingAdoption;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PendingAdoptionRepository extends CrudRepository<PendingAdoption, Long> {
    @Query("SELECT a FROM PendingAdoption a WHERE a.dogId = :dogId")
    Optional<PendingAdoption> findByDogId(@Param("dogId") long dogId);

    @Query("SELECT a FROM PendingAdoption a WHERE a.userId = :userId")
    Optional<List<PendingAdoption>> findByUserId(@Param("userId") long userId);

}
