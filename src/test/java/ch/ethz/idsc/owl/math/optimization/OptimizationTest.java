// code by astoll
package ch.ethz.idsc.owl.math.optimization;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class OptimizationTest extends TestCase {
  public void testSimple() {
    List<Integer> integerList = new LinkedList<>();
    integerList.add(1);
    integerList.add(2);
    integerList.add(3);
    integerList.add(-1);
    integerList.add(-2);
    integerList.add(-3);
    List<Function<Integer, Scalar>> featureFunctionVector = new LinkedList<>();
    featureFunctionVector.add(x -> RealScalar.of(x).abs());
    featureFunctionVector.add(RealScalar::of);
    List<Function<Scalar, Scalar>> scoringFunctionVector = Arrays.asList( //
        Norm._2::ofVector);
    OrderComparator<Scalar> totalScalarComparator = Order.comparator(Scalars::lessEquals);
    Optimization<Integer, Scalar, Scalar> opt = new Optimization(integerList, featureFunctionVector, scoringFunctionVector, totalScalarComparator);
    assertEquals(integerList, opt.inputs);
    assertEquals(featureFunctionVector, opt.featureFunctionVector);
    assertEquals(totalScalarComparator, opt.orderComparator);
  }
}
