// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.List;

public enum LexicographicTotalOrder {
  INSTANCE, //
  ;
  public Integer compare(List<Comparable> x, List<Comparable> y) {
    if (x.size() != y.size()) {
      throw new RuntimeException("Elements to compare not of same size");
    }
    int cmp = 0;
    for (int i = 0; i < x.size() && cmp == 0; i++) {
      cmp = Integer.signum(x.get(i).compareTo(y.get(i)));
    }
    return cmp;
  }
}
