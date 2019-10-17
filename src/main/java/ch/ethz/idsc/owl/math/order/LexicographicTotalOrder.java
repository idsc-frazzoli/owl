// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public enum LexicographicTotalOrder implements Comparator<List<Comparable>> {
  INSTANCE;
  // ---
  @Override // from Comparator
  public int compare(List<Comparable> x, List<Comparable> y) {
    if (x.size() != y.size())
      throw new RuntimeException("Elements to compare not of same size");
    // ---
    int cmp = 0;
    for (int index = 0; index < x.size() && cmp == 0; ++index)
      cmp = x.get(index).compareTo(y.get(index));
    return cmp;
  }
}
