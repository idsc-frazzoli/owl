// code by astoll
package ch.ethz.idsc.owl.demo.order;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.math.order.ProductOrderComparator;

public enum TensorProductOrder {
  ;
  /** @param length
   * @return */
  public static ProductOrderComparator comparator(int length) {
    return new ProductOrderComparator( //
        Stream.generate(() -> ScalarTotalOrder.INSTANCE) //
            .limit(length) //
            .collect(Collectors.toList()));
  }
}
