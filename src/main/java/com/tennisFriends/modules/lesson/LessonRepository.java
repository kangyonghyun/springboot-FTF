package com.tennisFriends.modules.lesson;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Lesson findByPath(String path);

    @EntityGraph(attributePaths = {"tags", "managers"})
    Lesson findLessonWithTagsByPath(String path);

    @EntityGraph(attributePaths = {"zones", "managers"})
    Lesson findLessonWithZonesByPath(String path);

    @EntityGraph(attributePaths = "managers")
    Lesson findLessonWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members")
    Lesson findLessonWithMembersByPath(String path);

    Lesson findLessonOnlyByPath(String path);
}
