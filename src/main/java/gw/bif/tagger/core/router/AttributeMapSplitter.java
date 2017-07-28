package gw.bif.tagger.core.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeMapSplitter {

  public List<Map<String, Map<String, String>>> split(Map<String, Map<String, String>> namespaceMap) {
    List<Map<String, Map<String, String>>> splittedMaps = new ArrayList<>();
    namespaceMap.entrySet().stream().forEach((entry) -> {
      Map<String, Map<String, String>> singleMap = new HashMap<>();
      singleMap.put(entry.getKey(), entry.getValue());
      splittedMaps.add(singleMap);
    });
    return splittedMaps;
  }
}
