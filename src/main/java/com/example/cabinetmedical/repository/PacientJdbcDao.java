package com.example.cabinetmedical.repository;

import com.example.cabinetmedical.Pacient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PacientJdbcDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final class PacientRowMapper implements RowMapper<Pacient> {
        @Override
        public Pacient mapRow(ResultSet rs, int rowNum) throws SQLException {
            Pacient pacient = new Pacient();
            pacient.setPacientID(rs.getLong("PacientID"));
            pacient.setCnp(rs.getString("CNP"));
            pacient.setNume(rs.getString("Nume"));
            pacient.setPrenume(rs.getString("Prenume"));
            pacient.setEmail(rs.getString("Email"));
            pacient.setParola(rs.getString("Parola"));
            pacient.setJudetDomiciliu(rs.getString("JudetDomiciliu"));
            pacient.setIsActive(rs.getBoolean("IsActive"));

            java.sql.Date dataNasteriiSql = rs.getDate("DataNasterii");
            if (dataNasteriiSql != null) {
                pacient.setDataNasterii(dataNasteriiSql.toLocalDate());
            }
            return pacient;
        }
    }

    private final PacientRowMapper MAPPER = new PacientRowMapper();
    private final String SELECT_BASE = "SELECT * FROM Pacient";

    public List<Pacient> findByNume(String nume) {
        String sql = SELECT_BASE + " WHERE IsActive = 1 AND UPPER(Nume) LIKE UPPER(?)";
        return jdbcTemplate.query(sql, MAPPER, "%" + nume + "%");
    }

    public List<Pacient> findByNumeAndPrenume(String nume, String prenume) {
        String sql = SELECT_BASE + " WHERE IsActive = 1 AND UPPER(Nume) LIKE UPPER(?) AND UPPER(Prenume) LIKE UPPER(?)";
        return jdbcTemplate.query(sql, MAPPER, "%" + nume + "%", "%" + prenume + "%");
    }

    public Pacient findById(Long id) {
        String sql = SELECT_BASE + " WHERE PacientID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void save(Pacient p) {
        String sql = """
            INSERT INTO Pacient (Nume, Prenume, CNP, Email, Parola, DataNasterii, JudetDomiciliu, IsActive)
            VALUES (?, ?, ?, ?, ?, ?, ?, 1)
        """;
        jdbcTemplate.update(sql, p.getNume(), p.getPrenume(), p.getCnp(), p.getEmail(), p.getParola(), p.getDataNasterii(), p.getJudetDomiciliu());
    }

    public void deactivate(Long id) {
        String sql = "UPDATE Pacient SET IsActive = 0 WHERE PacientID = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteDefinitiv(Long id) {
        jdbcTemplate.update("DELETE FROM DosarMedical WHERE PacientID = ?", id);

        jdbcTemplate.update("DELETE FROM DiagnosticPacient WHERE PacientID = ?", id);

        jdbcTemplate.update("DELETE FROM Programare WHERE PacientID = ?", id);

        jdbcTemplate.update("DELETE FROM Pacient WHERE PacientID = ?", id);
    }
}