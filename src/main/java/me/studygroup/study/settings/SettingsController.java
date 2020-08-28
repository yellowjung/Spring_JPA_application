package me.studygroup.study.settings;

import lombok.RequiredArgsConstructor;
import me.studygroup.study.account.AccountService;
import me.studygroup.study.account.CurrentUser;
import me.studygroup.study.donmain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/settings/profile";

    private final AccountService accountService;

    @GetMapping(SETTINGS_PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL) //ModelAttribute는 생략가능
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        //account 의 상태는 session에서 온거기 때문에 detached 상태 이기 때문에 한번 DB에 저장되어있는 놈이라
        //영속성 컨테스트는 아님 그래서 변경을 해도 변경이력을 감지하지 않아서 DB반영이 안됨
        //그래서 수정하고 save를 해줘야함 AccountService 확인할 것
        
        accountService.updateProfile(account, profile);
        //redirectAttributes 는 리다이렉트 시 한번 사용하고 없어질 녀석
        attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }
}
