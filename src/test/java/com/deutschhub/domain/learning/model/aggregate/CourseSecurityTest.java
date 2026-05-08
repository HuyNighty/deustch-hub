package com.deutschhub.domain.learning.model.aggregate;

import com.deutschhub.common.exception.BusinessException;
import com.deutschhub.common.exception.ErrorCode;
import com.deutschhub.domain.learning.model.entity.Lesson;
import com.deutschhub.domain.learning.model.entity.Section;
import com.deutschhub.domain.learning.model.valueobject.CEFRLevel;
import com.deutschhub.domain.learning.model.valueobject.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseSecurityTest {

    @Test
    void shouldRejectMutationWhenActorIsNotOwnerOrAdmin() {
        UUID ownerId = UUID.randomUUID();
        UUID anotherInstructor = UUID.randomUUID();
        Course course = Course.create("A1 Basics", "desc", CEFRLevel.A1, new Money(BigDecimal.ZERO), ownerId);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> course.addSection(Section.create("Section 1", "", 0), anotherInstructor, false));

        assertEquals(ErrorCode.COURSE_FORBIDDEN_ACTION, ex.getErrorCode());
    }

    @Test
    void shouldAllowAdminToMutateAnyCourse() {
        UUID ownerId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        Course course = Course.create("A1 Basics", "desc", CEFRLevel.A1, new Money(BigDecimal.ZERO), ownerId);

        course.addSection(Section.create("Section 1", "", 0), adminId, true);

        assertEquals(1, course.getSections().size());
    }

    @Test
    void shouldRejectMutationAfterSoftDelete() {
        UUID ownerId = UUID.randomUUID();
        Course course = Course.create("A1 Basics", "desc", CEFRLevel.A1, new Money(BigDecimal.ZERO), ownerId);
        course.softDelete(ownerId, false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> course.addSection(Section.create("Section 1", "", 0), ownerId, false));

        assertEquals(ErrorCode.COURSE_ALREADY_DELETED, ex.getErrorCode());
    }

    @Test
    void shouldRejectCourseStructureMutationWhenPublished() {
        UUID ownerId = UUID.randomUUID();
        Course course = Course.create("A1 Basics", "desc", CEFRLevel.A1, new Money(BigDecimal.ZERO), ownerId);
        Section section = Section.create("Section 1", "", 0);
        section.addLesson(Lesson.create("L1", "", "content", 15, CEFRLevel.A1, 0));
        course.addSection(section, ownerId, false);
        course.publish(ownerId, false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> course.addSection(Section.create("Section 2", "", 1), ownerId, false));

        assertEquals(ErrorCode.CANNOT_MODIFY_PUBLISHED_COURSE, ex.getErrorCode());
    }
}
