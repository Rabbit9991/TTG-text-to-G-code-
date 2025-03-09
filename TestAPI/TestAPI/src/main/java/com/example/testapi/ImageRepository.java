package com.example.testapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String> {

    @Query("SELECT i FROM Image i ORDER BY i.creationDate ASC")
    List<Image> findAllOrderByCreationDateAsc();
}
