package com.example.cabinetmedical;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
@Entity
@Table(name = "Pacient")
public class Pacient {

    private String email;
    private String parola;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PacientID")
    private Long pacientID;

    @Column(name = "CNP")
    private String cnp;

    @Column(name = "Nume")
    private String nume;

    @Column(name = "Prenume")
    private String prenume;

    @Column(name = "JudetNatal")
    private String judetNatal;

    @Column(name = "OrasNatal")
    private String orasNatal;

    @Column(name = "JudetDomiciliu")
    private String judetDomiciliu;

    @Column(name = "OrasDomiciliu")
    private String orasDomiciliu;

    @Column(name = "Strada")
    private String strada;

    @Column(name = "Numar")
    private String numar;

    @Column(name = "CodPostal")
    private String codPostal;

    @Column(name = "Sex")
    private String sex;

    @Column(name = "DataNasterii")
    private LocalDate dataNasterii;

    @Column(name = "CasaDeAsigurari")
    private String casaDeAsigurari;

    public Pacient() {}

    public Long getPacientID() { return pacientID; }
    public void setPacientID(Long pacientID) { this.pacientID = pacientID; }

    public String getCnp() { return cnp; }
    public void setCnp(String cnp) { this.cnp = cnp; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }

    public String getJudetNatal() { return judetNatal; }
    public void setJudetNatal(String judetNatal) { this.judetNatal = judetNatal; }
    public String getOrasNatal() { return orasNatal; }
    public void setOrasNatal(String orasNatal) { this.orasNatal = orasNatal; }
    public String getJudetDomiciliu() { return judetDomiciliu; }
    public void setJudetDomiciliu(String judetDomiciliu) { this.judetDomiciliu = judetDomiciliu; }
    public String getOrasDomiciliu() { return orasDomiciliu; }
    public void setOrasDomiciliu(String orasDomiciliu) { this.orasDomiciliu = orasDomiciliu; }
    public String getStrada() { return strada; }
    public void setStrada(String strada) { this.strada = strada; }
    public String getNumar() { return numar; }
    public void setNumar(String numar) { this.numar = numar; }
    public String getCodPostal() { return codPostal; }
    public void setCodPostal(String codPostal) { this.codPostal = codPostal; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public LocalDate getDataNasterii() { return dataNasterii; }
    public void setDataNasterii(LocalDate dataNasterii) { this.dataNasterii = dataNasterii; }

    public String getCasaDeAsigurari() { return casaDeAsigurari; }
    public void setCasaDeAsigurari(String casaDeAsigurari) { this.casaDeAsigurari = casaDeAsigurari; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getParola() { return parola; }
    public void setParola(String parola) { this.parola = parola; }

    private boolean isActive;
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }


}