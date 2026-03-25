package com.example.cabinetmedical.repository;

import com.example.cabinetmedical.Pacient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PacientRepository extends JpaRepository<Pacient, Long> {
    List<Pacient> findByNumeContainingIgnoreCase(String nume);
    List<Pacient> findByNumeContainingIgnoreCaseAndPrenumeContainingIgnoreCase(String nume, String prenume);
}