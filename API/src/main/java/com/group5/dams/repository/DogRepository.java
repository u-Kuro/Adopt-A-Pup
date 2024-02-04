package com.group5.dams.repository;

import com.group5.dams.model.Dog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DogRepository extends CrudRepository <Dog, Long> {
    @Query("SELECT d FROM Dog d WHERE d.id NOT IN (SELECT p.dogId FROM PendingAdoption p)")
    Optional<List<Dog>> findAllAvailableDogs();
}

