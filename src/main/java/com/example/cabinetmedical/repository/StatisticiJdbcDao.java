package com.example.cabinetmedical.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StatisticiJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getPacientiTopProgramariFiltrat(int minVizite) {
        String sql = """
            SELECT p.Nume, p.Prenume, COUNT(pr.ProgramareID) as NumarVizite
            FROM Pacient p
            JOIN Programare pr ON p.PacientID = pr.PacientID
            GROUP BY p.Nume, p.Prenume
            HAVING COUNT(pr.ProgramareID) >= ?
            ORDER BY NumarVizite DESC
        """;
        return jdbcTemplate.queryForList(sql, minVizite);
    }

    public List<Map<String, Object>> getTratamenteDetaliate() {
        String sql = """
            SELECT p.Nume, p.Prenume, t.Tratament, t.DurataZile
            FROM DosarMedical dm
            JOIN Pacient p ON dm.PacientID = p.PacientID
            JOIN Tratament t ON dm.TratamentID = t.TratamentID
        """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getPacientiFaraProgramari() {
        String sql = """
            SELECT Nume, Prenume, CNP 
            FROM Pacient 
            WHERE PacientID NOT IN (SELECT DISTINCT PacientID FROM Programare)
        """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getDiagnosticeFolosite() {
        String sql = """
            SELECT Denumire 
            FROM Diagnostic 
            WHERE DiagnosticID IN (SELECT DISTINCT DiagnosticID FROM DiagnosticPacient)
        """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getPacientiRecenti() {
        String sql = """
            SELECT Nume, Prenume, Email
            FROM Pacient
            WHERE PacientID IN (
                SELECT PacientID 
                FROM Programare 
                WHERE DataProgramata > DATEADD(day, -30, GETDATE())
            )
        """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getTratamentMaxim() {
        String sql = """
            SELECT Tratament, DurataZile
            FROM Tratament
            WHERE DurataZile = (SELECT MAX(DurataZile) FROM Tratament)
        """;
        return jdbcTemplate.queryForList(sql);
    }
}