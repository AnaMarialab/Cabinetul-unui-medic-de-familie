package com.example.cabinetmedical.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DosarJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> findIstoricConsultatii(Long pacientId) {
        String sql = """
            SELECT 
                c.Data, 
                c.Ora, 
                c.Motiv, 
                d.Denumire as Diagnostic
            FROM DiagnosticPacient dp
            JOIN Consultatie c ON dp.ConsultatieID = c.ConsultatieID
            JOIN Diagnostic d ON dp.DiagnosticID = d.DiagnosticID
            WHERE dp.PacientID = ?
            ORDER BY c.Data DESC
        """;
        return jdbcTemplate.queryForList(sql, pacientId);
    }

    public List<Map<String, Object>> findTratamente(Long pacientId) {
        String sql = """
            SELECT 
                t.Tratament, 
                t.DurataZile, 
                dm.Observatii,
                d.Denumire as DiagnosticCauza
            FROM DosarMedical dm
            JOIN Tratament t ON dm.TratamentID = t.TratamentID
            LEFT JOIN Diagnostic d ON t.DiagnosticID = d.DiagnosticID
            WHERE dm.PacientID = ?
        """;
        return jdbcTemplate.queryForList(sql, pacientId);
    }

    public void addConsultatieSiDiagnostic(Long pacientId, Long diagnosticId, String data, String ora, String motiv) {
        String sqlConsultatie = "INSERT INTO Consultatie (Data, Ora, Motiv) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlConsultatie, data, ora, motiv);

        Long consultatieId = jdbcTemplate.queryForObject("SELECT TOP 1 ConsultatieID FROM Consultatie ORDER BY ConsultatieID DESC", Long.class);

        String sqlLink = "INSERT INTO DiagnosticPacient (PacientID, DiagnosticID, ConsultatieID) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlLink, pacientId, diagnosticId, consultatieId);
    }

    public List<Map<String, Object>> findAllDiagnostice() {
        return jdbcTemplate.queryForList("SELECT DiagnosticID, Denumire, TratamentStandard FROM Diagnostic");
    }

    public void finalizeazaConsultatie(Long programareId, Long diagnosticId, String tratamentText, int durataZile, String observatii) {
        String sqlGetInfo = "SELECT PacientID, DataProgramata, OraProgramata, Motiv FROM Programare WHERE ProgramareID = ?";
        Map<String, Object> info = jdbcTemplate.queryForMap(sqlGetInfo, programareId);

        Long pacientId = ((Number) info.get("PacientID")).longValue();
        String data = info.get("DataProgramata").toString();
        String ora = info.get("OraProgramata").toString();
        String motiv = (String) info.get("Motiv");

        String sqlConsultatie = "INSERT INTO Consultatie (Data, Ora, Motiv) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlConsultatie, data, ora, motiv);

        Long consultatieId = jdbcTemplate.queryForObject("SELECT TOP 1 ConsultatieID FROM Consultatie ORDER BY ConsultatieID DESC", Long.class);

        String sqlDiag = "INSERT INTO DiagnosticPacient (PacientID, DiagnosticID, ConsultatieID) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlDiag, pacientId, diagnosticId, consultatieId);

        String sqlTratament = "INSERT INTO Tratament (DiagnosticID, Tratament, DurataZile) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlTratament, diagnosticId, tratamentText, durataZile);

        Long tratamentId = jdbcTemplate.queryForObject("SELECT TOP 1 TratamentID FROM Tratament ORDER BY TratamentID DESC", Long.class);

        String sqlDosar = "INSERT INTO DosarMedical (PacientID, TratamentID, Observatii) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlDosar, pacientId, tratamentId, observatii);

        String sqlUpdateProg = "UPDATE Programare SET Status = 'COMPLETED' WHERE ProgramareID = ?";
        jdbcTemplate.update(sqlUpdateProg, programareId);
    }
}