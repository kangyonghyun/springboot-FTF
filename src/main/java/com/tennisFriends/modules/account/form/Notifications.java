package com.tennisFriends.modules.account.form;

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
}
