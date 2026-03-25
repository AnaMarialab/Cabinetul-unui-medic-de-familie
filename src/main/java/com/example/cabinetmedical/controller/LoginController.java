package com.example.cabinetmedical.controller;

import com.example.cabinetmedical.Medic;
import com.example.cabinetmedical.Pacient;
import com.example.cabinetmedical.repository.AuthJdbcDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private AuthJdbcDao authDao;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String parola,
                               @RequestParam String tipUtilizator,
                               HttpSession session,
                               Model model) {

        if ("medic".equals(tipUtilizator)) {
            Medic medic = authDao.loginMedic(email, parola);
            if (medic != null) {
                session.setAttribute("user", medic);
                session.setAttribute("role", "MEDIC");
                return "redirect:/medic/dashboard";
            }
        } else if ("pacient".equals(tipUtilizator)) {
            Pacient pacient = authDao.loginPacient(email, parola);
            if (pacient != null) {
                session.setAttribute("user", pacient);
                session.setAttribute("role", "PACIENT");
                session.setAttribute("userId", pacient.getPacientID());
                return "redirect:/pacient/dashboard";
            }
        }
        model.addAttribute("error", "Email sau parola incorecta!");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}