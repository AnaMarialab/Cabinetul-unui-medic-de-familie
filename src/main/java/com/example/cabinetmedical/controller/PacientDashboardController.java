package com.example.cabinetmedical.controller;

import com.example.cabinetmedical.Pacient;
import com.example.cabinetmedical.repository.DosarJdbcDao;
import com.example.cabinetmedical.repository.ProgramareJdbcDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pacient")
public class PacientDashboardController {

    @Autowired
    private DosarJdbcDao dosarDao;

    @Autowired
    private ProgramareJdbcDao programareDao;

    private boolean isPacient(HttpSession session) {
        return session.getAttribute("user") != null && "PACIENT".equals(session.getAttribute("role"));
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isPacient(session)) {
            return "redirect:/login";
        }
        Pacient pacientConectat = (Pacient) session.getAttribute("user");
        Long pacientId = pacientConectat.getPacientID();

        model.addAttribute("pacient", pacientConectat);
        model.addAttribute("istoric", dosarDao.findIstoricConsultatii(pacientId));
        model.addAttribute("tratamente", dosarDao.findTratamente(pacientId));
        model.addAttribute("programari", programareDao.findByPacient(pacientId));

        model.addAttribute("contActiv", pacientConectat.getIsActive());

        return "dashboard_pacient";
    }

    @PostMapping("/cereProgramare")
    public String cereProgramare(@RequestParam("motiv") String motiv, HttpSession session) {
        if (!isPacient(session)) return "redirect:/login";

        Pacient p = (Pacient) session.getAttribute("user");

        if (!p.getIsActive()) {
            return "redirect:/pacient/dashboard";
        }

        programareDao.creazaCerere(p.getPacientID(), motiv);

        return "redirect:/pacient/dashboard";
    }
    @PostMapping("/anuleazaProgramare")
    public String anuleazaProgramare(@RequestParam("id") Long id, HttpSession session) {
        if (session.getAttribute("user") == null || !"PACIENT".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        programareDao.stergeProgramare(id);

        return "redirect:/pacient/dashboard";
    }
}