package me.studygroup.study.controller.account;

import org.dom4j.rule.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        return "account/sign-up";
    }
}
