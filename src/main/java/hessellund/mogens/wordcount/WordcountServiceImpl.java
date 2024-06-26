package hessellund.mogens.wordcount;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class WordcountServiceImpl implements WordcountService {

    /**
     * Creates a map of filenames, word count as strings. The word count counts case-insensitive occurrences of the word in the string.
     * @param filenameCharacter a String containing the characters used for filenames
     * @param strings A collection of strings in various cases and allowing for doublets
     * @param excludedStrings A collection of strings that should be excluded from the final result
     * @return a map using the characters in filenameCharacter as keys, and a string representation of the word count as value. Default as a blank String.
     */
    @Override
    public Map<String, String> createOutputMap(String filenameCharacter,
                                               Collection<String> strings,
                                               Collection<String> excludedStrings) {
        List<Word> excludedWords = loadWords(excludedStrings);
        List<Word> words = loadWords(strings);

        Map<Word, WordCount> wordWordCountMap = wordCountByWord(words, excludedWords);

        Map<String, List<WordByCount>> filenameMap = getFilenameMap(wordWordCountMap);

        return getOutputMap(filenameCharacter, filenameMap);
    }

    /**
     * Iterates over a list and count unique instances found in an excluded list. The comparison is case-insensitive
     * @param strings The list of strings to search through
     * @param excludedStrings List of words that should be excluded
     * @return count of individual instances
     */
    public long countExcluded(List<String> strings, List<String> excludedStrings) {
        if (excludedStrings == null || excludedStrings.isEmpty()) {
            return 0;
        }

        List<Word> words = loadWords(strings);
        List<Word> excludedWords = loadWords(excludedStrings);

        return words.stream()
                .filter(excludedWords::contains)
                .distinct()
                .count();
    }


    static Map<String, String> getOutputMap(String filenameCharacter, Map<String, List<WordByCount>> filenameMap) {
        return IntStream.range(0, filenameCharacter.length())
                .mapToObj(i -> String.valueOf(filenameCharacter.charAt(i)))
                .collect(Collectors.toMap(
                        c -> c,
                        c -> {
                            if (!filenameMap.containsKey(c)) {
                                return "";
                            }
                            return filenameMap.get(c).stream()
                                    .map(WordByCount::toString)
                                    .collect(Collectors.joining("\n", "", "\n"));
                        }
                ));
    }

    static List<Word> loadWords(Collection<String> strings) {
        return strings.stream()
                .filter(Objects::nonNull)
                .filter(w -> !w.isBlank())
                .map(Word::new)
                .toList();
    }

    static Map<Word, WordCount> wordCountByWord(List<Word> words, List<Word> excludedWords) {
        return words.stream()
                .filter(word -> !excludedWords.contains(word))
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                WordCount::new)));
    }

    static Map<String, List<WordByCount>> getFilenameMap(Map<Word, WordCount> wordsMap) {
        return wordsMap.entrySet().stream()
                .map(WordByCount::new)
                .collect(Collectors.groupingBy(
                                WordByCount::filename,
                                Collectors.mapping(o -> o, Collectors.toList())
                        )
                );
    }

}

record Word(String word) {
    Word(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("Word can not be blank");
        }
        this.word = word.trim().toUpperCase();
    }

    public String getFirstLetter() {
        return word.substring(0, 1);
    }
}

record WordCount(long count) {
}

record WordByCount(Word word, WordCount wordCount) {
    WordByCount(Map.Entry<Word, WordCount> entry) {
        this(entry.getKey(), entry.getValue());
    }

    String filename() {
        return word.getFirstLetter();
    }

    @Override
    public String toString() {
        return word.word() + " " + wordCount.count();
    }
}

