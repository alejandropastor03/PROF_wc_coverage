package es.upm.grise.profundizacion.wc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AppTest {

    // Fichero de prueba que se crea antes de ejecutar los tests
    private static Path testFile = Paths.get("ejemplo.txt");
    private static PrintStream originalOut = System.out;

    @BeforeAll
    public static void setup() throws IOException {
        // Creamos un fichero con texto para probar wc
        Files.writeString(
            testFile,
            "kjdbvws wonvwofjw\n" +
            "sdnfwijf ooj    kjndfohwouer 21374 vehf\n" +
            "jgfosj\n\n" +
            "skfjwoief ewjf\n\n\n" +
            "dkfgwoihgpw vs wepfjwfin",
            StandardCharsets.UTF_8
        );
    }

    @AfterAll
    public static void teardown() throws IOException {
        // Restauramos la salida estándar y borramos el fichero al terminar los tests
        System.setOut(originalOut);
        Files.deleteIfExists(testFile);
    }

    @Test
    public void testUsageMessageWhenNoArgs() {
        // Capturamos la salida por consola
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        // Ejecutamos el programa sin argumentos
        App.main(new String[] {});

        // Comprobamos que se muestra el mensaje de ayuda
        assertEquals(
            "Usage: wc [-clw file]",
            output.toString().trim()
        );
    }

    @Test
    public void testWrongArgumentsMessageWhenOnlyOneArg() {
        // Si no hay exactamente 2 argumentos debe mostrar error
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "-c" });

        assertTrue(output.toString().contains("Wrong arguments!"));
    }

    @Test
    public void testWrongArgumentsMessageWhenThreeArgs() {
        // Si hay más de 2 argumentos también debe mostrar error
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "-c", "file.txt", "extra" });

        assertTrue(output.toString().contains("Wrong arguments!"));
    }

    @Test
    public void testCannotFindFileMessage() {
        // Si el fichero no existe, debe avisar y terminar
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "-c", "no_existe_123.txt" });

        assertTrue(output.toString().contains("Cannot find file: no_existe_123.txt"));
    }

    @Test
    public void testCommandsDoNotStartWithDash() {
        /*
         * En el programa se comprueba que el primer argumento empiece por '-'.
         * Si no, debe mostrar el mensaje de error y salir.
         */
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "clw", testFile.toString() });

        assertTrue(output.toString().contains("The commands do not start with -"));
    }

    @Test
    public void testUnrecognizedCommand() {
        // Si ponemos una letra que no es c/l/w, debe mostrar error
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "-z", testFile.toString() });

        assertTrue(output.toString().contains("Unrecognized command: z"));
    }

    @Test
    public void testValidCCommandPrintsSomething() {
        /*
         * En este test solo comprobamos que el programa funciona cuando
         * se le pasa un fichero correcto y un comando válido.
         *
         * No comprobamos los números exactos que imprime (caracteres,
         * líneas o palabras) porque el contador de palabras de esta
         * práctica no cuenta palabras reales como el comando wc de Unix,
         * sino que suma los espacios, tabs y saltos de línea.
         *
         * Si aquí comprobáramos valores concretos, el test dependería
         * demasiado de cómo está implementado el contador y podría fallar
         * por cambios pequeños en el texto.
         *
         * Por eso, en este test solo verificamos que el programa no da
         * error y que imprime el nombre del fichero, y los cálculos
         * concretos se prueban en CounterTest.
         */
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "-c", testFile.toString() });

        // Al menos debe aparecer el nombre del fichero en la salida
        assertTrue(output.toString().contains(testFile.toString()));
    }

    @Test
    public void testValidLCommandPrintsSomething() {
        // Igual que el anterior pero con -l
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "-l", testFile.toString() });

        assertTrue(output.toString().contains(testFile.toString()));
    }

    @Test
    public void testValidWCommandPrintsSomething() {
        // Igual que el anterior pero con -w
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "-w", testFile.toString() });

        assertTrue(output.toString().contains(testFile.toString()));
    }

    @Test
    public void testMultipleCommandsOrder_clw() throws IOException {
        /*
         * Aquí sí comprobamos el orden de la salida, porque App
         * construye el string siguiendo el orden de los comandos.
         *
         * Para no depender del fichero grande, creamos uno más pequeño:
         * "a b\tc\n" -> chars=6, lines=1, words(separadores)=3
         */
        Path smallFile = Paths.get("mini.txt");
        Files.writeString(smallFile, "a b\tc\n", StandardCharsets.UTF_8);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "-clw", smallFile.toString() });

        // Debe salir en ese orden: 6, 1, 3 y luego el nombre del fichero
        assertTrue(output.toString().contains("\t6\t1\t3\t" + smallFile.toString()));

        Files.deleteIfExists(smallFile);
    }

    @Test
    public void testMultipleCommandsOrder_wlc() throws IOException {
        // Igual que el test anterior, pero cambiando el orden de los comandos
        Path smallFile = Paths.get("mini2.txt");
        Files.writeString(smallFile, "a b\tc\n", StandardCharsets.UTF_8);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        App.main(new String[] { "-wlc", smallFile.toString() });

        // Orden: words=3, lines=1, chars=6
        assertTrue(output.toString().contains("\t3\t1\t6\t" + smallFile.toString()));

        Files.deleteIfExists(smallFile);
    }

}
