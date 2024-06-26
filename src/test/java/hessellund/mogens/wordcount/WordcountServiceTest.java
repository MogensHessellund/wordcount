package hessellund.mogens.wordcount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WordcountServiceTest {
    static final List<String> excludedWordsString = List.of("Den", "lille", "Ole", "og", "paraplyen", "ham", "kender", "alle", "småfolk", "i", "byen");

    static final List<String> denLilleOleString = List.of("Og", "Og", "når", "om", "ost", "morgenen", "solen", "skinner",
            "da", "vågner", "de", "med", "OST", "små", "røde", "kinder",
            "og", "takke", "Gud","oSt",  "kender", "hvad", "ost", "de", "har", "drømt",
            "og", "kysse", "osT", "paraplyen", "i", "paraplyen", "ost", "ømt");

    List<Word> denLilleOle;
    List<Word> excludedWords;

    static final String filenameLetters = "ABCDEFGHIJKLMNOPQRSTUVXYZWÆØÅ";

    @BeforeEach
    void setUp() {
        excludedWords = WordcountServiceImpl.loadWords(excludedWordsString);
        denLilleOle = WordcountServiceImpl.loadWords(denLilleOleString);
    }

    @Test
    void loadWords() {
        //when
        List<Word> words = WordcountServiceImpl.loadWords(denLilleOleString);

        //then
        assertTrue(words.contains(new Word("KINDER")));
        assertTrue(words.contains(new Word("morgenen")));
        assertFalse(words.contains(new Word("hammer")));
        assertEquals(words.get(0), words.get(1));
        assertEquals(34, words.size());
    }

    @Test
    void loadEmptyWords() {
        //given
        List<String> stringList = List.of("I", "en", " ", "" , "kælder", "", " sort", "som ", "kul");
        //when
        List<Word> words = WordcountServiceImpl.loadWords(stringList);

        //then
        List<Word> expectedWordList = List.of(new Word("I"),
                new Word("EN"),
                new Word("KÆLDER"),
                new Word("SORT"),
                new Word("SOM"),
                new Word("KUL"));
        assertTrue(words.containsAll(expectedWordList));
        assertEquals(expectedWordList.size(), words.size());
    }

    @Test
    void loadNullWords() {
        //given
        List<String> stringList = new ArrayList<>(List.of("I", "en", " ", "kælder", " sort", "som ", "kul"));
        stringList.add(1, null);
        stringList.add(3, null);
        stringList.add(5, null);
        //when
        List<Word> words = WordcountServiceImpl.loadWords(stringList);

        //then
        List<Word> expectedWordList = List.of(new Word("I"),
                new Word("EN"),
                new Word("KÆLDER"),
                new Word("SORT"),
                new Word("SOM"),
                new Word("KUL"));
        assertTrue(words.containsAll(expectedWordList));
        assertEquals(expectedWordList.size(), words.size());
    }

    @Test
    void createWord() {
        //when
        Word hund = new Word("hund");

        //assert
        assertEquals("HUND", hund.word());
        assertEquals("H", hund.getFirstLetter());
    }

    @Test
    void createEmptyWord() {
        //assert
        assertThrowsExactly(IllegalArgumentException.class, () -> new Word(""), "Expecting a RuntimeException when trying to create a Word with an empty string");
    }

    @Test
    void countExcluded() {
        //given
        WordcountService wordcountService = new WordcountServiceImpl();

        //when
        long countExcluded = wordcountService.countExcluded(denLilleOleString, excludedWordsString);

        //then
        assertEquals(4, countExcluded);
    }

    @Test
    void countExcludedEmpty() {
        //given
        WordcountService wordcountService = new WordcountServiceImpl();

        //when
        long countExcluded = wordcountService.countExcluded(denLilleOleString, List.of());

        //then
        assertEquals(0, countExcluded);
    }

    @Test
    void testWordCountByWord() {
        //when
        Map<Word, WordCount> wordWordCountMap = WordcountServiceImpl.wordCountByWord(denLilleOle, excludedWords);

        //then
        assertEquals(new WordCount(2), wordWordCountMap.get(new Word("de")));
        assertEquals(new WordCount(1), wordWordCountMap.get(new Word("KySsE")));
        assertEquals(new WordCount(6), wordWordCountMap.get(new Word("ost")));
        assertNull(wordWordCountMap.get(new Word("banan")));
    }

    @Test
    void testFilenameMap() {
        //when
        Map<String, List<WordByCount>> filenameMap = WordcountServiceImpl.getFilenameMap(WordcountServiceImpl.wordCountByWord(denLilleOle, excludedWords));

        //then
        List<WordByCount> mWords = filenameMap.get("M");

        assertEquals(2, mWords.size());
        assertNull(filenameMap.get("L"));
    }

    @Test
    void testCreateOutputMap() {
        //given
        WordcountService wordcountService = new WordcountServiceImpl();

        //when
        Map<String, String> outputMap = wordcountService.createOutputMap(filenameLetters, denLilleOleString, excludedWordsString);
        assertTrue(outputMap.get("M").contains("MORGENEN"));
        assertTrue(outputMap.get("L").isBlank());
    }

    public static void main(String[] args) {
        WordcountService wordcountService = new WordcountServiceImpl();
        Map<String, String> outputMap = wordcountService.createOutputMap(filenameLetters, denLilleOleString, excludedWordsString);

        outputMap.forEach((key, value) -> System.out.println(key + "\t" + value));
    }
}