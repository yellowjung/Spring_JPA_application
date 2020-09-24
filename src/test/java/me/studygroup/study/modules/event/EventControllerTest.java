package me.studygroup.study.modules.event;

import me.studygroup.study.controller.account.WithAccount;
import me.studygroup.study.modules.account.Account;
import me.studygroup.study.modules.study.Study;
import me.studygroup.study.modules.study.StudyControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest extends StudyControllerTest{

    @Autowired
    EventService eventService;
    @Autowired EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("dony")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account test = createAccount("test");
        Study study = createStudy("test-study", test);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, test);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account dony = accountRepository.findByNickname("dony");
        isAccepted(dony, event);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @WithAccount("dony")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account all = createAccount("all");
        Study study = createStudy("test-study", all);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, all);

        Account may = createAccount("may");
        Account june = createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account test = accountRepository.findByNickname("dony");
        isNotAccepted(test, event);
    }

    @Test
    @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
    @WithAccount("dony")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account dony = accountRepository.findByNickname("dony");
        Account test = createAccount("test");
        Account may = createAccount("may");
        Study study = createStudy("test-study", test);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, test);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, dony);
        eventService.newEnrollment(event, test);

        isAccepted(may, event);
        isAccepted(dony, event);
        isNotAccepted(test, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(test, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, dony));
    }

    @Test
    @DisplayName("참가신청 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @WithAccount("dony")
    void not_accepterd_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account dony = accountRepository.findByNickname("dony");
        Account test = createAccount("test");
        Account may = createAccount("may");
        Study study = createStudy("test-study", test);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, test);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, test);
        eventService.newEnrollment(event, dony);

        isAccepted(may, event);
        isAccepted(test, event);
        isNotAccepted(dony, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(test, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, dony));
    }

    private void isNotAccepted(Account account, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @WithAccount("dony")
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account test = createAccount("test");
        Study study = createStudy("test-study", test);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, test);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account dony = accountRepository.findByNickname("dony");
        isNotAccepted(dony, event);
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, study, account);
    }
}