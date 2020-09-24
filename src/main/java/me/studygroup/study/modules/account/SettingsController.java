package me.studygroup.study.modules.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.studygroup.study.modules.account.form.NicknameForm;
import me.studygroup.study.modules.account.form.Notifications;
import me.studygroup.study.modules.account.form.PasswordForm;
import me.studygroup.study.modules.account.form.Profile;
import me.studygroup.study.modules.tag.Tag;
import me.studygroup.study.modules.tag.TagForm;
import me.studygroup.study.modules.zone.Zone;
import me.studygroup.study.modules.account.validator.NicknameValidator;
import me.studygroup.study.modules.account.validator.PasswordFormValidator;
import me.studygroup.study.modules.tag.TagRepository;
import me.studygroup.study.modules.tag.TagService;
import me.studygroup.study.modules.zone.ZoneForm;
import me.studygroup.study.modules.zone.ZoneRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static me.studygroup.study.modules.account.SettingsController.ROOT;
import static me.studygroup.study.modules.account.SettingsController.SETTINGS;

@Controller
@RequestMapping(ROOT + SETTINGS)
@RequiredArgsConstructor
public class SettingsController {

    public static final String ROOT = "/";
    public static final String SETTINGS = "settings";
    public static final String PROFILE = "/profile";
    public static final String PASSWORD = "/password";
    static final String NOTIFICATIONS = "/notifications";
    static final String ACCOUNT = "/account";
    public static final String TAGS = "/tags";
    public static final String ZONES = "/zones";

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;
    private final TagService tagService;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping(PROFILE)
    public String updateProfileForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        //model.addAttribute(new Profile(account));
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS + PROFILE;
    }

    @PostMapping(PROFILE) //ModelAttribute는 생략가능
    public String updateProfile(@CurrentAccount Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS + PROFILE;
        }

        //account 의 상태는 session에서 온거기 때문에 detached 상태 이기 때문에 한번 DB에 저장되어있는 놈이라
        //영속성 컨테스트는 아님 그래서 변경을 해도 변경이력을 감지하지 않아서 DB반영이 안됨
        //그래서 수정하고 save를 해줘야함 AccountService 확인할 것
        
        accountService.updateProfile(account, profile);
        //redirectAttributes 는 리다이렉트 시 한번 사용하고 없어질 녀석
        attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
        return "redirect:/" + SETTINGS + PROFILE;
    }

    @GetMapping(PASSWORD)
    public String updatePasswordForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS + PASSWORD;
    }

    @PostMapping(PASSWORD)
    public String updatePassword(@CurrentAccount Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS + PASSWORD;
        }
        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
        return "redirect:/" + SETTINGS + PASSWORD;
    }

    @GetMapping(NOTIFICATIONS)
    public String updateNotificationsForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        //model.addAttribute(new Notifications(account));
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return SETTINGS + NOTIFICATIONS;
    }

    @PostMapping(NOTIFICATIONS)
    public String updateNotifications(@CurrentAccount Account account, @Valid Notifications notifications, Errors errors,
                                      Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + NOTIFICATIONS;
        }

        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
        return "redirect:/" + SETTINGS + NOTIFICATIONS;
    }

    @GetMapping(ACCOUNT)
    public String updateAccountForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS + ACCOUNT;
    }

    @PostMapping(ACCOUNT)
    public String updateAccount(@CurrentAccount Account account, @Valid NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS + ACCOUNT;
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
        return "redirect:/" + SETTINGS + ACCOUNT;
    }

    @GetMapping(TAGS)
    public String updateTags(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags",tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allTags));

        return SETTINGS + TAGS;
    }

    @PostMapping(TAGS + "/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @RequestBody TagForm tagForm){
//        String title = tagForm.getTagTitle();
//
////        Tag tag = tagRepository.findByTitle(title).orElseGet(()-> tagRepository.save(Tag.builder()
////        .title(tagForm.getTagTitle())
////        .build()));
//
//        Tag tag = tagRepository.findByTitle(title);
//        if(tag == null){
//            tag = tagRepository.save(Tag.builder().title(title).build());
//        }
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        accountService.addTag(account, tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping(TAGS + "/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null){
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account, tag);

        return ResponseEntity.ok().build();
    }

    @GetMapping(ZONES)
    public String updateZonesForm(@CurrentAccount Account account, Model model) throws JsonProcessingException{
        model.addAttribute(account);

        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        return "settings" + ZONES;
    }

    @PostMapping(ZONES + "/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ZONES + "/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }
}
