package com.example.cabinetmedical.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ProgramareJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void creazaCerere(Long pacientId, String motiv) {
        String sql = "INSERT INTO Programare (PacientID, Motiv, Status, DataCerere) VALUES (?, ?, 'PENDING', GETDATE())";
        jdbcTemplate.update(sql, pacientId, motiv);
    }

    public List<Map<String, Object>> findByPacient(Long pacientId) {
        String sql = "SELECT * FROM Programare WHERE PacientID = ? ORDER BY DataCerere DESC";
        return jdbcTemplate.queryForList(sql, pacientId);
    }

    public List<Map<String, Object>> findCereriInAsteptare() {
        String sql = """
            SELECT pr.ProgramareID, pr.Motiv, pr.DataCerere, p.Nume, p.Prenume, p.CNP
            FROM Programare pr
            JOIN Pacient p ON pr.PacientID = p.PacientID
            WHERE pr.Status = 'PENDING'
            ORDER BY pr.DataCerere ASC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    public void aprobaProgramare(Long programareId, String data, String ora) {
        String sql = "UPDATE Programare SET DataProgramata = ?, OraProgramata = ?, Status = 'APPROVED' WHERE ProgramareID = ?";
        jdbcTemplate.update(sql, data, ora, programareId);
    }

    public List<Map<String, Object>> findAllApprovedProgramari() {
        String sql = """
            SELECT pr.ProgramareID, pr.DataProgramata, pr.OraProgramata, pr.Motiv, p.Nume, p.Prenume, p.CNP
            FROM Programare pr
            JOIN Pacient p ON pr.PacientID = p.PacientID
            WHERE pr.Status = 'APPROVED' 
            ORDER BY pr.DataProgramata ASC, pr.OraProgramata ASC
        """;
        return jdbcTemplate.queryForList(sql);
    }
    public void stergeProgramare(Long programareId) {
        String sql = "DELETE FROM Programare WHERE ProgramareID = ? AND Status = 'PENDING'";
        jdbcTemplate.update(sql, programareId);
    }
}