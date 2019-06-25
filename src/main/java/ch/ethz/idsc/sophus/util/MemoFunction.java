// code by jph
package ch.ethz.idsc.sophus.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/** A memo function stores return values of a given function in hash map, so that the function
 * is not evaluated a second time, but instead, a lookup using the map is performed.
 * 
 * Since the memo function returns references, the function values should be immutable.
 * 
 * Wrapping a memo function around a memo function returns the given function. */
public class MemoFunction<K, V> implements Function<K, V>, Serializable {
  /** @param function non-null
   * @return given function if given function is instance of MemoFunction */
  public static <K, V> Function<K, V> wrap(Function<K, V> function) {
    return function instanceof MemoFunction // prevent inception
        ? function
        : new MemoFunction<>(Objects.requireNonNull(function));
  }

  // ---
  private final Map<K, V> memo = new HashMap<>();
  private final Function<K, V> function;

  private MemoFunction(Function<K, V> function) {
    this.function = function;
  }

  @Override
  public V apply(K key) {
    V value = memo.get(key);
    if (Objects.isNull(value)) {
      value = function.apply(key);
      memo.put(key, value);
    }
    return value;
  }
}
