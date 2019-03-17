// code by astoll
package ch.ethz.idsc.owl.math.optimization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.order.GenericLexicographicComparator;
import ch.ethz.idsc.owl.math.order.Order;
import ch.ethz.idsc.owl.math.order.OrderComparator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class OptimizationTest extends TestCase {
  public void testSimple() {
    Set<Integer> integerList = new HashSet<>();
    integerList.add(1);
    integerList.add(2);
    integerList.add(3);
    integerList.add(-1);
    integerList.add(-2);
    integerList.add(-3);
    List<Function<Integer, Scalar>> functionVector = new LinkedList<>();
    functionVector.add(x -> RealScalar.of(x).abs());
    functionVector.add(RealScalar::of);
    List<OrderComparator> comparators = Arrays.asList( //
        Order.comparator(Scalars::lessEquals), //
        Order.comparator(Scalars::lessEquals)); //
    GenericLexicographicComparator genericLexicographicOrder = new GenericLexicographicComparator(comparators);
    Optimization<Integer, Scalar> opt = new Optimization(integerList, functionVector, genericLexicographicOrder);
    assertEquals(integerList, opt.inputs);
    assertEquals(functionVector, opt.functionVector);
    assertEquals(genericLexicographicOrder, opt.orderComparator);
  }
  // public void testObjectiveSpace(){
  // Set<Integer> integerList = new HashSet<>();
  // integerList.add(1);
  // integerList.add(2);
  // integerList.add(2);
  // integerList.add(-1);
  // integerList.add(-2);
  // integerList.add(-3);
  // List<Function<Integer, Scalar>> functionVector = new LinkedList<>();
  // functionVector.add(x -> RealScalar.of(x).abs());
  // Optimization<Integer, Scalar> opt = new Optimization(integerList, functionVector);
  // System.out.println(opt.inputsInObjectiveSpace());
  // }
}
