// code by gjoel
package ch.ethz.idsc.owl.data.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.tensor.Scalar;

public interface ObservingExpandInterface<T extends StateCostNode> extends ExpandInterface<T> {
  /** @return whether active or not
   * DEFAULT: inactive */
  default boolean isObserving() {
    return false;
  }

  /** dump observation log of current expand
   * @param observations (current time, current best node's {@link StateCostNode#costFromRoot()})
   * DEFAULT: print short summary */
  default void process(Map<Double, Scalar> observations) {
    if (!observations.isEmpty()) {
      System.out.print("time until first solution:\t");
      observations.keySet().stream().findFirst().ifPresent(System.out::println);
      // ---
      List<Scalar> values = new ArrayList<>(observations.values());
      System.out.println("cost gradient:\t" + values.get(0) + " -> " + Lists.getLast(values));
    }
  }

  /** @param first node
   * DEFAULT: do nothing */
  default void processFirst(T first) {
    // ---
  }

  /** @param last node
   * DEFAULT: do nothing */
  default void processLast(T last) {
    // ---
  }
}
