package com.deutschhub.domain.learning.model.aggregate;

import com.deutschhub.common.exception.BusinessException;
import com.deutschhub.common.exception.ErrorCode;
import com.deutschhub.domain.learning.model.valueobject.Progress;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnrollmentSecurityTest {

    @Test
    void shouldRejectNonOwnerProgressUpdate() {
        UUID ownerId = UUID.randomUUID();
        Enrollment enrollment = Enrollment.create(ownerId, UUID.randomUUID());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> enrollment.updateProgress(Progress.createInitial(30), UUID.randomUUID(), false));

        assertEquals(ErrorCode.ENROLLMENT_FORBIDDEN_ACTION, ex.getErrorCode());
    }

    @Test
    void shouldAllowAdminToUpdateProgress() {
        UUID ownerId = UUID.randomUUID();
        Enrollment enrollment = Enrollment.create(ownerId, UUID.randomUUID());

        enrollment.startLearning(ownerId, false);
        enrollment.updateProgress(Progress.createInitial(30), UUID.randomUUID(), true);

        assertEquals(30, enrollment.getProgress().getCompletionPercentage());
    }

    @Test
    void shouldRejectMutationAfterDelete() {
        UUID ownerId = UUID.randomUUID();
        Enrollment enrollment = Enrollment.create(ownerId, UUID.randomUUID());
        enrollment.softDelete(ownerId, false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> enrollment.drop(ownerId, false));

        assertEquals(ErrorCode.ENROLLMENT_ALREADY_DELETED, ex.getErrorCode());
    }
}
