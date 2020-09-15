package me.studygroup.study.study;

import lombok.RequiredArgsConstructor;
import me.studygroup.study.donmain.Account;
import me.studygroup.study.donmain.Study;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository repository;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy = repository.save(study);
        newStudy.addManager(account);

        return newStudy;
    }
}
