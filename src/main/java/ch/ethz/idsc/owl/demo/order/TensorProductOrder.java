// code by astoll
package ch.ethz.idsc.owl.demo.order;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.math.order.GenericProductOrderComparator;
import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.Scalars;

public class TensorProductOrder extends GenericProductOrderComparator {
  public TensorProductOrder(List<OrderComparator> comparatorList) {
    super(comparatorList);
  }

  public static TensorProductOrder createTensorProductOrder(int dim) {
    List<OrderComparator> comparators = new LinkedList<>();
    for (int i = 0; i < dim; ++i) {
      comparators.add(Order.comparator(Scalars::lessEquals));
    }
    return new TensorProductOrder(comparators);
  }
}
