package com.example.cabinetmedical.repository;

import com.example.cabinetmedical.Medic;
import com.example.cabinetmedical.Pacient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Medic loginMedic(String email, String parola) {
        if ("doctor@cabinet.ro".equals(email) && "admin123".equals(parola)) {
            Medic medicFals = new Medic();
            medicFals.setNume("Admin");
            medicFals.setPrenume("Doctor");
            medicFals.setEmail(email);
            return medicFals;
        }
        return null;
    }

    public Pacient loginPacient(String email, String parola) {
        String sql = "SELECT PacientID, Nume, Prenume, Email, IsActive FROM Pacient WHERE Email = ? AND Parola = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Pacient.class), email, parola);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}