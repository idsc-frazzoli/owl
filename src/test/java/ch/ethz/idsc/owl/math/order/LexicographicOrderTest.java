// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.demo.order.ScalarTotalOrder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class LexicographicOrderTest extends TestCase {
  public void testTotalLexciographic() {
    OrderComparator<Scalar> comparator1 = ScalarTotalOrder.INSTANCE;
    List<OrderComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    // FIXME ASTOLL warnings
    LexicographicOrder<Scalar> lexicographicOrder = new LexicographicOrder<>(comparatorList);
    List<Scalar> x = new LinkedList<>();
    x.add(RealScalar.of(1));
    x.add(RealScalar.of(2));
    x.add(RealScalar.of(2));
    List<Scalar> y = new LinkedList<>();
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(2));
    assertEquals(OrderComparison.STRICTLY_PRECEDES, lexicographicOrder.compare(x, y));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, lexicographicOrder.compare(y, x));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
  }

  public void testPartialLexicographic() {
    OrderComparator<Scalar> comparator1 = new Order<>((x, y) -> Scalars.divides(x.abs(), y.abs()));
    List<OrderComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    LexicographicOrder<Scalar> lexicographicOrder = new LexicographicOrder<>(comparatorList);
    List<Scalar> x = new LinkedList<>();
    x.add(RealScalar.of(2));
    x.add(RealScalar.of(-5));
    x.add(RealScalar.of(2));
    List<Scalar> y = new LinkedList<>();
    y.add(RealScalar.of(6));
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(2));
    List<Scalar> z = new LinkedList<>();
    z.add(RealScalar.of(7));
    z.add(RealScalar.of(2));
    z.add(RealScalar.of(2));
    assertEquals(OrderComparison.STRICTLY_PRECEDES, lexicographicOrder.compare(x, y));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, lexicographicOrder.compare(y, x));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
    assertEquals(OrderComparison.INCOMPARABLE, lexicographicOrder.compare(x, z));
    assertEquals(OrderComparison.INCOMPARABLE, lexicographicOrder.compare(y, z));
    assertEquals(OrderComparison.INCOMPARABLE, lexicographicOrder.compare(z, x));
    assertEquals(OrderComparison.INCOMPARABLE, lexicographicOrder.compare(z, y));
  }

  public void testException() {
    OrderComparator<Scalar> comparator1 = new Order<>((x, y) -> Scalars.divides(x.abs(), y.abs()));
    List<OrderComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    LexicographicOrder<Scalar> lexicographicOrder = new LexicographicOrder<>(comparatorList);
    List<Scalar> x = Tensors.vector(2, -5, 2).stream().map(Scalar.class::cast).collect(Collectors.toList());
    List<Scalar> y = Tensors.vector(6, 2).stream().map(Scalar.class::cast).collect(Collectors.toList());
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
    try {
      lexicographicOrder.compare(x, y);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      lexicographicOrder.compare(y, x);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    OrderComparator<Scalar> comparator1 = new Order<>((x, y) -> Scalars.divides(x.abs(), y.abs()));
    List<OrderComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    LexicographicOrder<Scalar> lexicographicOrder = Serialization.copy(new LexicographicOrder<>(comparatorList));
    List<Scalar> x = Tensors.vector(2, -5, 2).stream().map(Scalar.class::cast).collect(Collectors.toList());
    List<Scalar> y = Tensors.vector(6, 2).stream().map(Scalar.class::cast).collect(Collectors.toList());
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
  }
  // public void testQualifiesForComparison() {
  // // TODO ASTOLL
  //
  // }
  // FIXME ASTOLL Does not work for comparator of different types
  // public void testMultipleDifferentComparators() {
  // UniversalComparator<Integer> comparator1 = UniversalOrder.comparator((x, y)-> x<=y);
  // UniversalComparator<Scalar> comparator2 = UniversalOrder.comparator(Scalars::lessEquals);
  // List<UniversalComparator<Object>> comparatorList = new LinkedList<>();
  // comparatorList.add(comparator1);
  // comparatorList.add(comparator2);
  // }
}
