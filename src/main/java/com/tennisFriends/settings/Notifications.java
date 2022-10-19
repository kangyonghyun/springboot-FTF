package com.tennisFriends.settings;

import com.tennisFriends.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notifications {

    private boolean friendCreatedByEmail;
    private boolean friendCreatedByWeb;
    private boolean friendEnrollmentResultByEmail;
    private boolean friendEnrollmentResultByWeb;
    private boolean friendUpdatedByEmail;
    private boolean friendUpdatedByWeb;
    public Notifications(Account account) {
        this.friendCreatedByEmail = account.isFriendCreatedByEmail();
        this.friendCreatedByWeb = account.isFriendCreatedByWeb();
        this.friendEnrollmentResultByEmail = account.isFriendEnrollmentResultByEmail();
        this.friendEnrollmentResultByWeb = account.isFriendEnrollmentResultByWeb();
        this.friendUpdatedByEmail = account.isFriendUpdatedByEmail();
        this.friendUpdatedByWeb = account.isFriendCreatedByWeb();
    }
}
