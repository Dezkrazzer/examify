package com.examify.controller;

import com.examify.app.AppSession;
import com.examify.model.QuestionType;
import com.examify.model.Question;
import com.examify.model.MultipleChoiceQuestion;
import com.examify.model.Exam;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExamEditorTest {

    @Test
    void testDrafDanSoalBaru() {
        List<Question> list = List.of(new MultipleChoiceQuestion(1, "Soal 1", 10.0, List.of("A", "B", "C", "D"), 'A'));
        Exam u = new Exam("BIN-01", "Bahasa Indonesia", "TOKEN1", 20, 70.0, list);
        AppSession.getInstance().setUjianAktif(u);

        // simulasi draf
        int maxId = u.getDaftarSoal().stream().mapToInt(Question::getIdSoal).max().orElse(0);
        assertEquals(1, maxId);
    }
}
