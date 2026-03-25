package com.example.cabinetmedical.controller;

import com.example.cabinetmedical.Medic;
import com.example.cabinetmedical.Pacient;
import com.example.cabinetmedical.repository.DosarJdbcDao;
import com.example.cabinetmedical.repository.PacientJdbcDao;
import com.example.cabinetmedical.repository.ProgramareJdbcDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.cabinetmedical.repository.StatisticiJdbcDao;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/medic")
public class MedicController {

    @Autowired
    private StatisticiJdbcDao statisticiDao;

    @Autowired
    private PacientJdbcDao pacientDao;

    @Autowired
    private DosarJdbcDao dosarDao;

    @Autowired
    private ProgramareJdbcDao programareDao;
    private boolean isMedic(HttpSession session) {
        return session.getAttribute("user") != null && "MEDIC".equals(session.getAttribute("role"));
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isMedic(session)) {
            return "redirect:/login";
        }

        Medic medicConectat = (Medic) session.getAttribute("user");
        model.addAttribute("medic", medicConectat);

        if (!model.containsAttribute("pacienti")) {
            model.addAttribute("pacienti", new ArrayList<Pacient>());
        }

        model.addAttribute("cereri", programareDao.findCereriInAsteptare());

        return "dashboard_medic";
    }

    @GetMapping("/cauta")
    public String cautaPacient(@RequestParam("termenCautare") String termen, HttpSession session, Model model) {
        if (!isMedic(session)) {
            return "redirect:/login";
        }

        List<Pacient> rezultate;
        if (termen == null || termen.trim().isEmpty()) {
            rezultate = new ArrayList<>();
        } else {
            String[] cuvinte = termen.trim().split("\\s+");
            if (cuvinte.length == 1) {
                rezultate = pacientDao.findByNume(cuvinte[0]);
            } else {
                rezultate = pacientDao.findByNumeAndPrenume(cuvinte[0], cuvinte[1]);
            }
        }

        model.addAttribute("medic", session.getAttribute("user"));
        model.addAttribute("pacienti", rezultate);
        model.addAttribute("termenCautare", termen);

        model.addAttribute("cereri", programareDao.findCereriInAsteptare());

        return "dashboard_medic";
    }

    @PostMapping("/aprobaProgramare")
    public String aprobaProgramare(@RequestParam("programareId") Long programareId,
                                   @RequestParam("data") String data,
                                   @RequestParam("ora") String ora,
                                   HttpSession session) {
        if (!isMedic(session)) return "redirect:/login";

        programareDao.aprobaProgramare(programareId, data, ora);

        return "redirect:/medic/dashboard";
    }

    @GetMapping("/agenda")
    public String veziAgenda(HttpSession session, Model model) {
        if (!isMedic(session)) return "redirect:/login";

        model.addAttribute("medic", session.getAttribute("user"));
        model.addAttribute("agenda", programareDao.findAllApprovedProgramari());

        return "agenda_medic";
    }

    @GetMapping("/pacient/nou")
    public String formularAdaugarePacient(HttpSession session, Model model) {
        if (!isMedic(session)) return "redirect:/login";
        model.addAttribute("medic", session.getAttribute("user"));
        model.addAttribute("pacientNou", new Pacient());
        return "adaugare_pacient";
    }

    @GetMapping("/consultatie/finalizare/{id}")
    public String formularFinalizare(@PathVariable("id") Long programareId, HttpSession session, Model model) {
        if (!isMedic(session)) return "redirect:/login";

        model.addAttribute("medic", session.getAttribute("user"));
        model.addAttribute("programareId", programareId);
        model.addAttribute("listaDiagnostice", dosarDao.findAllDiagnostice());

        return "finalizare_consultatie";
    }

    @PostMapping("/consultatie/salveaza")
    public String salveazaConsultatie(@RequestParam("programareId") Long programareId,
                                      @RequestParam("diagnosticId") Long diagnosticId,
                                      @RequestParam("tratament") String tratament,
                                      @RequestParam("durata") int durata,
                                      @RequestParam("observatii") String observatii,
                                      HttpSession session) {
        if (!isMedic(session)) return "redirect:/login";
        dosarDao.finalizeazaConsultatie(programareId, diagnosticId, tratament, durata, observatii);
        return "redirect:/medic/agenda";
    }
    @PostMapping("/pacient/salveaza")
    public String salveazaPacient(@ModelAttribute Pacient pacientNou, HttpSession session, Model model) {
        if (!isMedic(session)) return "redirect:/login";

        String cnp = pacientNou.getCnp();

        if (cnp == null || cnp.length() != 13 || !cnp.matches("\\d+")) {
            model.addAttribute("medic", session.getAttribute("user"));
            model.addAttribute("pacientNou", pacientNou);
            model.addAttribute("eroare", "CNP-ul trebuie să conțină exact 13 cifre!");
            return "adaugare_pacient";
        }

        pacientDao.save(pacientNou);

        return "redirect:/medic/dashboard";
    }

    @PostMapping("/pacient/sterge")
    public String stergePacient(@RequestParam("id") Long id, HttpSession session) {
        if (!isMedic(session)) return "redirect:/login";

        pacientDao.deactivate(id);

        return "redirect:/medic/dashboard";
    }

@PostMapping("/pacient/stergeDefinitiv")
public String stergeDefinitiv(@RequestParam("id") Long id, HttpSession session) {
    if (!isMedic(session)) return "redirect:/login";

    pacientDao.deleteDefinitiv(id);

    return "redirect:/medic/dashboard";
}

    @GetMapping("/pacient/{id}")
    public String veziDosarPacient(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (!isMedic(session)) return "redirect:/login";

        Pacient pacient = pacientDao.findById(id);
        if (pacient == null) {
            return "redirect:/medic/dashboard";
        }

        model.addAttribute("medic", session.getAttribute("user"));
        model.addAttribute("pacient", pacient);
        model.addAttribute("istoric", dosarDao.findIstoricConsultatii(id));
        model.addAttribute("tratamente", dosarDao.findTratamente(id));
        model.addAttribute("listaDiagnostice", dosarDao.findAllDiagnostice());

        return "dosar_medical";
    }

    @PostMapping("/pacient/{id}/adaugaConsultatie")
    public String adaugaConsultatie(@PathVariable("id") Long id,
                                    @RequestParam("diagnosticId") Long diagnosticId,
                                    @RequestParam("motiv") String motiv,
                                    @RequestParam("data") String data,
                                    @RequestParam("ora") String ora,
                                    HttpSession session) {
        if (!isMedic(session)) return "redirect:/login";

        dosarDao.addConsultatieSiDiagnostic(id, diagnosticId, data, ora, motiv);

        return "redirect:/medic/pacient/" + id;
    }
    @GetMapping("/statistici")
    public String veziStatistici(@RequestParam(name = "minVizite", defaultValue = "1") int minVizite,
                                 HttpSession session, Model model) {
        if (!isMedic(session)) return "redirect:/login";

        model.addAttribute("medic", session.getAttribute("user"));

        model.addAttribute("topPacienti", statisticiDao.getPacientiTopProgramariFiltrat(minVizite));
        model.addAttribute("minViziteSelectat", minVizite);
        model.addAttribute("tratamenteDetaliate", statisticiDao.getTratamenteDetaliate());
        model.addAttribute("pacientiFaraVizite", statisticiDao.getPacientiFaraProgramari());
        model.addAttribute("diagnosticeFolosite", statisticiDao.getDiagnosticeFolosite());
        model.addAttribute("pacientiRecenti", statisticiDao.getPacientiRecenti());
        model.addAttribute("tratamentMaxim", statisticiDao.getTratamentMaxim());

        List<Pacient> totiPacientii = pacientDao.findByNume("");

        if (!totiPacientii.isEmpty()) {
            double varstaMedie = totiPacientii.stream()
                    .filter(p -> p.getDataNasterii() != null)
                    .mapToInt(p -> Period.between(p.getDataNasterii(), LocalDate.now()).getYears())
                    .average()
                    .orElse(0.0);

            Optional<Pacient> celMaiTanar = totiPacientii.stream()
                    .filter(p -> p.getDataNasterii() != null)
                    .max(Comparator.comparing(Pacient::getDataNasterii));

            Optional<Pacient> celMaiBatran = totiPacientii.stream()
                    .filter(p -> p.getDataNasterii() != null)
                    .min(Comparator.comparing(Pacient::getDataNasterii));

            model.addAttribute("varstaMedie", String.format("%.1f", varstaMedie));
            model.addAttribute("celMaiTanar", celMaiTanar.orElse(new Pacient()));
            model.addAttribute("celMaiBatran", celMaiBatran.orElse(new Pacient()));
            model.addAttribute("procesareJava", true);
        } else {
            model.addAttribute("procesareJava", false);
        }

        return "statistici_medic";
    }
}