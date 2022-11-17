package com.tennisFriends.event;

import com.tennisFriends.domain.Account;
import com.tennisFriends.domain.Enrollment;
import com.tennisFriends.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByEventAndAccount(Event event, Account account);
}
