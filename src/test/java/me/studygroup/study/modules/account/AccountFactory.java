package me.studygroup.study.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired AccountRepository accountRepository;

    public Account createAccount(String nickname){
        Account test = new Account();
        test.setNickname(nickname);
        test.setEmail(nickname + "@email.com");
        accountRepository.save(test);
        return test;
    }
}
