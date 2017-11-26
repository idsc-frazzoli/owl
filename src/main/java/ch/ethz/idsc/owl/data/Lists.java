// code by jph
package ch.ethz.idsc.owl.data;

import java.util.List;

public enum Lists {
  ;
  /** @param list
   * @return last element in given list
   * @throws Exception list is empty */
  public static <T> T getLast(List<T> list) {
    return list.get(list.size() - 1);
  }
}
