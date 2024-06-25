package hessellund.mogens.wordcount;

import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DirectoryAdapter {

    /**
     * Iterates over files in a directory and generates a list of words. The file containing excluded words is ignored
     * @param dirPathName the pathName of the directory
     * @param excludedFileName fileName for the file containing excluded words
     * @return list of strings
     */
    public static List<String> readStringsFromFilesInDirectory(String dirPathName, String excludedFileName) {
        Path dirPath = Paths.get(dirPathName);
        Path excludedFile = Paths.get(dirPathName, excludedFileName);

        List<String> allWords = new ArrayList<>();
        DirectoryStream<Path> stream;
        try {
            stream = Files.newDirectoryStream(dirPath);

            for (Path path : stream) {
                if (Files.isRegularFile(path) && !path.equals(excludedFile)) {
                    List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                    allWords.addAll(extractWords(lines));
                }
            }

            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return allWords;
    }

    /**
     * Reads the strings from a file into a list of strings.
     * @param dirPathName the pathName of the directory
     * @param fileName filename for the file to read from
     * @return list of strings
     */
    public static List<String> readStringsFromFile(String dirPathName, String fileName) {
        Path filePath = Paths.get(dirPathName, fileName);

        List<String> allWords = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            allWords.addAll(extractWords(lines));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return allWords;
    }

    private static List<String> extractWords(List<String> lines) {
        return lines.stream()
                .map(line -> line.split("\\s+"))
                .flatMap(Arrays::stream)
                .filter(word -> !word.isEmpty())
                .map(word -> word.replaceAll("[.,]$", ""))
                .collect(Collectors.toList());
    }

    /**
     * Writes out a map of filenames,String
     * @param dirPathName pathname to put files
     * @param subPath extended to pathname, e.g. 'out'
     * @param wordcountFiles map of filenames,String
     */
    public static void writeWordCounts(String dirPathName, String subPath, Map<String, String> wordcountFiles) {
        for (Map.Entry<String, String> str : wordcountFiles.entrySet()) {
            Path filepath = Paths.get(dirPathName, subPath, str.getKey());

            try {
                Files.writeString(filepath, str.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
