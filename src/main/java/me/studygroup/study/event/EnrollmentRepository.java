package me.studygroup.study.event;

import me.studygroup.study.domain.Account;
import me.studygroup.study.domain.Enrollment;
import me.studygroup.study.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByEventAndAccount(Event event, Account account);

    Enrollment findByEventAndAccount(Event event, Account account);
}
