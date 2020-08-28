package me.studygroup.study.settings;

import lombok.Data;
import me.studygroup.study.donmain.Account;

@Data
public class Profile {

    private String bio;

    private String url;

    private String occupation;

    private String location;

    public Profile(Account account){
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
