package me.studygroup.study.modules.study.validator;

import lombok.RequiredArgsConstructor;
import me.studygroup.study.modules.study.StudyRepository;
import me.studygroup.study.modules.study.form.StudyForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {

    private final StudyRepository studyRepository;


    @Override
    public boolean supports(Class<?> aClass) {
        return StudyForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        StudyForm studyForm = (StudyForm)o;
        if(studyRepository.existsByPath(studyForm.getPath())){
            errors.rejectValue("path","wrong.path","해당 스터디 경로값를 사용할 수 없습니다.");
        }
    }
}
