package com.examify.io;

import com.examify.exception.DataFormatException;
import com.examify.model.TeamMember;
import com.examify.model.TeamMember.Sosial;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamReaderTest {

    private final TeamReader reader = new TeamReader();

    @Test
    void sosialKosongMenjadiTidakAda() throws DataFormatException {
        TeamMember a = reader.parseBaris("123|Akbar Maulana Putra||https://a.id|| |", 1);
        assertEquals("AM", a.getInisial());
        assertEquals("https://a.id", a.getTautan(Sosial.WEBSITE).orElseThrow());
        assertTrue(a.getTautan(Sosial.GITHUB).isEmpty());
        assertTrue(a.getTautan(Sosial.INSTAGRAM).isEmpty());
        assertTrue(a.getTautan(Sosial.LINKEDIN).isEmpty());
    }

    @Test
    void cukupNimDanNama() throws DataFormatException {
        TeamMember a = reader.parseBaris("123|Budi", 1);
        assertEquals("", a.getFoto());
        assertTrue(a.getTautan(Sosial.LINKEDIN).isEmpty());
    }

    @Test
    void namaKosongDitolak() {
        assertThrows(DataFormatException.class, () -> reader.parseBaris("123|", 1));
    }
}
