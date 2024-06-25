package hessellund.mogens.wordcount;

import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectoryAdapter {

    public static List<String> readStringsFromFilesInDirectory(String dirPathName, String excludedFileName) {
        Path dirPath = Paths.get(dirPathName);

        List<String> allWords = new ArrayList<>();
        DirectoryStream<Path> stream;
        try {
            stream = Files.newDirectoryStream(dirPath);

            for (Path path : stream) {
                if (Files.isRegularFile(path) && !path.getFileName().endsWith(excludedFileName)) {
                    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                    allWords = extractWords(lines);
                }
            }

            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return allWords;
    }

    public static List<String> readStringsFromFile(String dirPathName, String excludedFileName) {
        Path filePath = Paths.get(dirPathName, excludedFileName);

        List<String> allWords;
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            allWords = extractWords(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return allWords;
    }

    private static List<String> extractWords(List<String> lines) {
        List<String> allWords = new ArrayList<>();
        for (String line : lines) {
            String[] words = line.split("\\W+"); // Split on non-word characters
            for (String word : words) {
                if (!word.isEmpty()) {
                    allWords.add(word);
                }
            }
        }
        return allWords;
    }

    public static void writeWordCounts(String dirPathName, Map<String, String> wordcountFiles) {
        for (Map.Entry<String, String> stringStringEntry : wordcountFiles.entrySet()) {
            Path filepath = Paths.get(dirPathName, "out", stringStringEntry.getKey());

            try {
                Files.writeString(filepath, stringStringEntry.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
