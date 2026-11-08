package com.examify.io;

import com.examify.exception.FormatDataException;
import com.examify.model.AnggotaTim;
import com.examify.model.AnggotaTim.Sosial;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimReaderTest {

    private final TimReader reader = new TimReader();

    @Test
    void sosialKosongMenjadiTidakAda() throws FormatDataException {
        AnggotaTim a = reader.parseBaris("123|Akbar Maulana Putra||https://a.id|| |", 1);
        assertEquals("AM", a.getInisial());
        assertEquals("https://a.id", a.getTautan(Sosial.WEBSITE).orElseThrow());
        assertTrue(a.getTautan(Sosial.GITHUB).isEmpty());
        assertTrue(a.getTautan(Sosial.INSTAGRAM).isEmpty());
        assertTrue(a.getTautan(Sosial.LINKEDIN).isEmpty());
    }

    @Test
    void cukupNimDanNama() throws FormatDataException {
        AnggotaTim a = reader.parseBaris("123|Budi", 1);
        assertEquals("", a.getFoto());
        assertTrue(a.getTautan(Sosial.LINKEDIN).isEmpty());
    }

    @Test
    void namaKosongDitolak() {
        assertThrows(FormatDataException.class, () -> reader.parseBaris("123|", 1));
    }
}
