// code by jph
package ch.ethz.idsc.owl.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Anagrams {
  ;
  private static List<Character> represent(char[] text) {
    List<Character> list = new ArrayList<>();
    for (char c : text)
      list.add(c);
    Collections.sort(list);
    return list;
  }

  public static void showAnagrams(List<String> texts) {
    Map<List<Character>, Long> map = texts.stream() //
        .collect(Collectors.groupingBy(t -> represent(t.toCharArray()), HashMap::new, Collectors.counting()));
    map.entrySet().stream() //
        .filter(entry -> 1 < entry.getValue()) // remove singletons
        .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // sort according to multiplicity
        .forEach(System.out::println);
  }

  public static void main(String[] args) {
    List<String> anagrams = new ArrayList<String>();
    anagrams.add("code");
    anagrams.add("cdoe");
    anagrams.add("ecde");
    anagrams.add("anagram");
    anagrams.add("aangram");
    anagrams.add("anagrma");
    anagrams.add("clever");
    anagrams.add("unicorn");
    showAnagrams(anagrams);
  }
}