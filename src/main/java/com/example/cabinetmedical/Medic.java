package com.example.cabinetmedical;

public class Medic {
    private Long medicID;
    private String nume;
    private String prenume;
    private String email;
    private String parola;

    public Medic() {}

    public Long getMedicID() { return medicID; }
    public void setMedicID(Long medicID) { this.medicID = medicID; }
    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getPrenume() { return prenume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getParola() { return parola; }
    public void setParola(String parola) { this.parola = parola; }
}