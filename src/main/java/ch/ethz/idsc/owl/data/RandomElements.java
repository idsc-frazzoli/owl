// method from https://stackoverflow.com/questions/4702036/take-n-random-elements-from-a-liste
// adapted by jph
package ch.ethz.idsc.owl.data;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** Quote:
 * "We can take advantage of the Durstenfeld's algorithm (the most popular
 * Fisher-Yates variant in our days).
 * 
 * Durstenfeld's solution is to move the "struck" numbers to the end of the
 * list by swapping them with the last unstruck number at each iteration.
 * 
 * Due to the above, we don't need to shuffle the whole list, but run the
 * loop for as many steps as the number of elements required to return.
 * The algorithm ensures that the last N elements at the end of the list
 * are 100% random if we used a perfect random function."
 * https://stackoverflow.com/questions/4702036/take-n-random-elements-from-a-liste */
public enum RandomElements {
  ;
  /** Hint: function is not thread safe
   * 
   * @param <T>
   * @param list
   * @param n non-negative
   * @param random
   * @return unmodifiable list
   * @throws Exception if n is negative */
  public static <T> List<T> of(List<T> list, int n, Random random) {
    int length = list.size();
    if (length <= n)
      return Collections.unmodifiableList(list);
    int lo = length - n;
    for (int index = length - 1; lo <= index; --index)
      Collections.swap(list, index, random.nextInt(index + 1));
    return Collections.unmodifiableList(list.subList(length - n, length));
  }

  /** Hint: function is not thread safe
   * 
   * @param <T>
   * @param list
   * @param n non-negative
   * @return unmodifiable list
   * @throws Exception if n is negative */
  public static <T> List<T> of(List<T> list, int n) {
    return of(list, n, ThreadLocalRandom.current());
  }
}
