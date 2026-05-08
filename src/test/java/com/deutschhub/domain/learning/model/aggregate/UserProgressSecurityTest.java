package com.deutschhub.domain.learning.model.aggregate;

import com.deutschhub.common.exception.BusinessException;
import com.deutschhub.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserProgressSecurityTest {

    @Test
    void shouldRejectNonOwnerMutation() {
        UUID ownerId = UUID.randomUUID();
        UserProgress progress = UserProgress.create(ownerId, UUID.randomUUID(), UUID.randomUUID());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> progress.recordSectionCompletion(UUID.randomUUID(), false));

        assertEquals(ErrorCode.USER_PROGRESS_FORBIDDEN_ACTION, ex.getErrorCode());
    }

    @Test
    void shouldAllowAdminMutation() {
        UUID ownerId = UUID.randomUUID();
        UserProgress progress = UserProgress.create(ownerId, UUID.randomUUID(), UUID.randomUUID());

        progress.recordSectionCompletion(UUID.randomUUID(), true);

        assertEquals(1, progress.getCompletedSections());
    }

    @Test
    void shouldRejectMutationAfterSoftDelete() {
        UUID ownerId = UUID.randomUUID();
        UserProgress progress = UserProgress.create(ownerId, UUID.randomUUID(), UUID.randomUUID());
        progress.softDelete(ownerId, false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> progress.recordSectionCompletion(ownerId, false));

        assertEquals(ErrorCode.USER_PROGRESS_ALREADY_DELETED, ex.getErrorCode());
    }
}
