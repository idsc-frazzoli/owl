// code by jph
package ch.ethz.idsc.sophus.math;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/** stores return values of function in hash map,
 * so that function is not evaluated a second time
 * but instead a lookup using the map is performed */
public class MemoFunction<K, V> implements Function<K, V>, Serializable {
  /** @param function non-null
   * @return */
  public static <K, V> Function<K, V> wrap(Function<K, V> function) {
    return new MemoFunction<>(Objects.requireNonNull(function));
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
