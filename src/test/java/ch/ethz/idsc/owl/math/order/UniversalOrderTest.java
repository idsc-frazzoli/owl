package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class UniversalOrderTest extends TestCase {
  public void testTotalOrderIntegers() {
    UniversalComparator<Integer> totalScalarComparator = UniversalOrder.comparator((x, y) -> x <= y);
    assertEquals(UniversalComparison.STRICTLY_PRECEDES, totalScalarComparator.compare(4, 5));
    assertEquals(UniversalComparison.INDIFFERENT, totalScalarComparator.compare(5, 5));
    assertEquals(UniversalComparison.STRICTLY_SUCCEDES, totalScalarComparator.compare(6, 5));
  }

  public void testTotalOrderScalars() {
    UniversalComparator<Scalar> totalScalarComparator = UniversalOrder.comparator(Scalars::lessEquals);
    assertEquals(UniversalComparison.STRICTLY_PRECEDES, totalScalarComparator.compare(RealScalar.of(5), RealScalar.of(7)));
    assertEquals(UniversalComparison.INDIFFERENT, totalScalarComparator.compare(RealScalar.of(5), RealScalar.of(5)));
    assertEquals(UniversalComparison.STRICTLY_SUCCEDES, totalScalarComparator.compare(RealScalar.of(5), RealScalar.of(-5)));
  }

  public void testPartialOrderScalars() {
    UniversalComparator<Scalar> partialScalarComparator = UniversalOrder.comparator((x, y) -> Scalars.divides(x.abs(), y.abs()));
    assertEquals(UniversalComparison.STRICTLY_PRECEDES, partialScalarComparator.compare(RealScalar.of(2), RealScalar.of(4)));
    assertEquals(UniversalComparison.STRICTLY_SUCCEDES, partialScalarComparator.compare(RealScalar.of(4), RealScalar.of(2)));
    assertEquals(UniversalComparison.INDIFFERENT, partialScalarComparator.compare(RealScalar.of(5), RealScalar.of(5)));
    assertEquals(UniversalComparison.INDIFFERENT, partialScalarComparator.compare(RealScalar.of(5), RealScalar.of(-5)));
    assertEquals(UniversalComparison.INCOMPARABLE, partialScalarComparator.compare(RealScalar.of(5), RealScalar.of(7)));
    assertEquals(UniversalComparison.INCOMPARABLE, partialScalarComparator.compare(RealScalar.of(7), RealScalar.of(5)));
  }

  public void testPreorderScalars() {
    UniversalComparator<Scalar> preorderScalarComparator = UniversalOrder.comparator(Scalars::divides);
    assertEquals(UniversalComparison.STRICTLY_PRECEDES, preorderScalarComparator.compare(RealScalar.of(2), RealScalar.of(4)));
    assertEquals(UniversalComparison.STRICTLY_SUCCEDES, preorderScalarComparator.compare(RealScalar.of(4), RealScalar.of(2)));
    assertEquals(UniversalComparison.INDIFFERENT, preorderScalarComparator.compare(RealScalar.of(5), RealScalar.of(5)));
    assertEquals(UniversalComparison.INDIFFERENT, preorderScalarComparator.compare(RealScalar.of(5), RealScalar.of(-5)));
    assertEquals(UniversalComparison.INCOMPARABLE, preorderScalarComparator.compare(RealScalar.of(5), RealScalar.of(7)));
    assertEquals(UniversalComparison.INCOMPARABLE, preorderScalarComparator.compare(RealScalar.of(7), RealScalar.of(5)));
  }
}
