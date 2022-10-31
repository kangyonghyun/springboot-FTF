package com.tennisFriends.lesson;

import com.tennisFriends.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    boolean existsByPath(String path);

    Lesson findByPath(String path);
}
