package es.upm.grise.profundizacion.wc;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CounterTest {

    @Test
    public void emptyInput_shouldReturnZeros() throws IOException {
        // Si no hay caracteres, todo se queda a 0
        BufferedReader reader = new BufferedReader(new StringReader(""));
        Counter counter = new Counter(reader);

        assertEquals(0, counter.getNumberCharacters());
        assertEquals(0, counter.getNumberLines());
        assertEquals(0, counter.getNumberWords());
    }

    @Test
    public void singleChar_shouldCountOneCharacter() throws IOException {
        // Un carácter sin separador: chars=1, lines=0, words=0
        BufferedReader reader = new BufferedReader(new StringReader("x"));
        Counter counter = new Counter(reader);

        assertEquals(1, counter.getNumberCharacters());
        assertEquals(0, counter.getNumberLines());
        assertEquals(0, counter.getNumberWords());
    }

    @Test
    public void oneSpace_shouldCountAsSeparatorWord() throws IOException {
        // Un espacio cuenta como caracter y además como "word" (separador)
        BufferedReader reader = new BufferedReader(new StringReader(" "));
        Counter counter = new Counter(reader);

        assertEquals(1, counter.getNumberCharacters());
        assertEquals(0, counter.getNumberLines());
        assertEquals(1, counter.getNumberWords());
    }

    @Test
    public void textEndingWithSpace_shouldIncreaseWordsByOne() throws IOException {
        // "hello " -> chars=6, words=1 porque hay 1 espacio al final
        BufferedReader reader = new BufferedReader(new StringReader("hello "));
        Counter counter = new Counter(reader);

        assertEquals(6, counter.getNumberCharacters());
        assertEquals(0, counter.getNumberLines());
        assertEquals(1, counter.getNumberWords());
    }

    @Test
    public void textWithoutSeparatorAtEnd_shouldNotCountLastWord() throws IOException {
        /*
         * Este Counter no cuenta palabras reales.
         * Si no hay separadores, numberWords se queda a 0.
         */
        BufferedReader reader = new BufferedReader(new StringReader("hello"));
        Counter counter = new Counter(reader);

        assertEquals(5, counter.getNumberCharacters());
        assertEquals(0, counter.getNumberLines());
        assertEquals(0, counter.getNumberWords());
    }

    @Test
    public void onlyNewlines_shouldCountLinesAndWords() throws IOException {
        // "\n\n" -> chars=2, lines=2, words=2 (cada '\n' cuenta como separador)
        BufferedReader reader = new BufferedReader(new StringReader("\n\n"));
        Counter counter = new Counter(reader);

        assertEquals(2, counter.getNumberCharacters());
        assertEquals(2, counter.getNumberLines());
        assertEquals(2, counter.getNumberWords());
    }

    @Test
    public void mixOfSpacesTabsAndNewlines_shouldCountAllSeparators() throws IOException {
        /*
         * "one two\tthree\nfour  "
         * Separadores: espacio (1) + tab (1) + newline (1) + dos espacios (2) = 5
         * lines: 1 (por el \n)
         * chars: longitud total del string = 20
         */
        String text = "one two\tthree\nfour  ";
        BufferedReader reader = new BufferedReader(new StringReader(text));
        Counter counter = new Counter(reader);

        assertEquals(20, counter.getNumberCharacters());
        assertEquals(1, counter.getNumberLines());
        assertEquals(5, counter.getNumberWords());
    }

    @Test
    public void simpleCase_shouldCoverSpaceTabNewline() throws IOException {
        // "a b\tc\n" -> chars=6, lines=1, words(separadores)=3
        String text = "a b\tc\n";
        BufferedReader reader = new BufferedReader(new StringReader(text));
        Counter counter = new Counter(reader);

        assertEquals(6, counter.getNumberCharacters());
        assertEquals(1, counter.getNumberLines());
        assertEquals(3, counter.getNumberWords());
    }
}

