package hessellund.mogens.wordcount;

import java.util.Collection;
import java.util.Map;

public interface WordcountService {
    Map<String, String> createOutputMap(String filenameCharacter,
                                        Collection<String> strings,
                                        Collection<String> excludedStrings);
}
