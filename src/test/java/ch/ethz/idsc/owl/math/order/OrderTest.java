package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class OrderTest extends TestCase {
  public void testTotalOrderIntegers() {
    OrderComparator<Integer> totalScalarComparator = Order.comparator((x, y) -> x <= y);
    assertEquals(OrderComparison.STRICTLY_PRECEDES, totalScalarComparator.compare(4, 5));
    assertEquals(OrderComparison.INDIFFERENT, totalScalarComparator.compare(5, 5));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, totalScalarComparator.compare(6, 5));
  }

  public void testTotalOrderScalars() {
    OrderComparator<Scalar> totalScalarComparator = Order.comparator(Scalars::lessEquals);
    assertEquals(OrderComparison.STRICTLY_PRECEDES, totalScalarComparator.compare(RealScalar.of(5), RealScalar.of(7)));
    assertEquals(OrderComparison.INDIFFERENT, totalScalarComparator.compare(RealScalar.of(5), RealScalar.of(5)));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, totalScalarComparator.compare(RealScalar.of(5), RealScalar.of(-5)));
  }

  public void testPartialOrderScalars() {
    OrderComparator<Scalar> partialScalarComparator = Order.comparator((x, y) -> Scalars.divides(x.abs(), y.abs()));
    assertEquals(OrderComparison.STRICTLY_PRECEDES, partialScalarComparator.compare(RealScalar.of(2), RealScalar.of(4)));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, partialScalarComparator.compare(RealScalar.of(4), RealScalar.of(2)));
    assertEquals(OrderComparison.INDIFFERENT, partialScalarComparator.compare(RealScalar.of(5), RealScalar.of(5)));
    assertEquals(OrderComparison.INDIFFERENT, partialScalarComparator.compare(RealScalar.of(5), RealScalar.of(-5)));
    assertEquals(OrderComparison.INCOMPARABLE, partialScalarComparator.compare(RealScalar.of(5), RealScalar.of(7)));
    assertEquals(OrderComparison.INCOMPARABLE, partialScalarComparator.compare(RealScalar.of(7), RealScalar.of(5)));
  }

  public void testPreorderScalars() {
    OrderComparator<Scalar> preorderScalarComparator = Order.comparator(Scalars::divides);
    assertEquals(OrderComparison.STRICTLY_PRECEDES, preorderScalarComparator.compare(RealScalar.of(2), RealScalar.of(4)));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, preorderScalarComparator.compare(RealScalar.of(4), RealScalar.of(2)));
    assertEquals(OrderComparison.INDIFFERENT, preorderScalarComparator.compare(RealScalar.of(5), RealScalar.of(5)));
    assertEquals(OrderComparison.INDIFFERENT, preorderScalarComparator.compare(RealScalar.of(5), RealScalar.of(-5)));
    assertEquals(OrderComparison.INCOMPARABLE, preorderScalarComparator.compare(RealScalar.of(5), RealScalar.of(7)));
    assertEquals(OrderComparison.INCOMPARABLE, preorderScalarComparator.compare(RealScalar.of(7), RealScalar.of(5)));
  }
}
