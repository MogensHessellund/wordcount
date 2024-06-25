package hessellund.mogens.wordcount;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Controller
public class WordcountController implements CommandLineRunner {

    public static final String FILENAME_CHARACTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ";
    final WordcountService wordcountService;

    public WordcountController(WordcountService wordcountService) {
        this.wordcountService = wordcountService;
    }

    public Map<String, String> createWordcountFiles(List<String> strings, List<String> excluded) {
        Map<String, String> outputMap = wordcountService.createOutputMap(FILENAME_CHARACTER, strings, excluded);

        long countExcluded = wordcountService.countExcluded(strings, excluded);
        outputMap.put("excluded_count", Long.toString(countExcluded));
        return outputMap;
    }

    @Override
    public void run(String... args) {
       //String dirPathName = "/home/mogenshessellund/tmp/wordcount";

        if (args.length == 0) {
            System.out.println();
            System.out.println("=====================================================================");
            System.out.println();
            System.out.println("Usage java -jar wordcountapplication.jar path-to-files-to-be-counted");
            System.out.println();
            System.out.println("=====================================================================");
            System.exit(0);
        }
        String dirPathName = args[0];

        List<String> strings = DirectoryAdapter.readStringsFromFilesInDirectory(dirPathName, "excluded");
        List<String> excludedString = DirectoryAdapter.readStringsFromFile(dirPathName, "excluded");

        Map<String, String> wordcountFiles = createWordcountFiles(strings, excludedString);

        DirectoryAdapter.writeWordCounts(dirPathName, "out", wordcountFiles);
    }

}
