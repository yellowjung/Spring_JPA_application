package me.studygroup.study.settings;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.studygroup.study.donmain.Account;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor //Profile Update 시 Profile binding을 위해서 기본 생성자가 필요함
public class Profile {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    public Profile(Account account){
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
