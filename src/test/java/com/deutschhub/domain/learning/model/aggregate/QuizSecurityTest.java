package com.deutschhub.domain.learning.model.aggregate;

import com.deutschhub.common.exception.BusinessException;
import com.deutschhub.common.exception.ErrorCode;
import com.deutschhub.domain.learning.model.entity.Question;
import com.deutschhub.domain.learning.model.valueobject.QuestionType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuizSecurityTest {

    @Test
    void shouldRejectNonOwnerMutation() {
        UUID ownerId = UUID.randomUUID();
        Quiz quiz = Quiz.createDraft(UUID.randomUUID(), ownerId);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> quiz.updateTitle("New title", UUID.randomUUID(), false));

        assertEquals(ErrorCode.QUIZ_FORBIDDEN_ACTION, ex.getErrorCode());
    }

    @Test
    void shouldAllowAdminToMutateQuiz() {
        UUID ownerId = UUID.randomUUID();
        Quiz quiz = Quiz.createDraft(UUID.randomUUID(), ownerId);

        quiz.updateTitle("Admin changed", UUID.randomUUID(), true);

        assertEquals("Admin changed", quiz.getTitle());
    }

    @Test
    void shouldRejectMutationAfterSoftDelete() {
        UUID ownerId = UUID.randomUUID();
        Quiz quiz = Quiz.createDraft(UUID.randomUUID(), ownerId);
        quiz.softDelete(ownerId, false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> quiz.addQuestion(Question.create(quiz.getId(), "Q1", 10, QuestionType.SINGLE_CHOICE), ownerId, false));

        assertEquals(ErrorCode.QUIZ_ALREADY_DELETED, ex.getErrorCode());
    }
}
