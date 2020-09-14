package me.studygroup.study.settings.form;


import lombok.Data;
import lombok.NoArgsConstructor;
import me.studygroup.study.donmain.Account;

@Data
//@NoArgsConstructor
public class Notifications {

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;
}
