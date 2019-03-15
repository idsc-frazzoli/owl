// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class UniversalLexicographicOrderTest extends TestCase {
  public void testTotalLexciographic() {
    UniversalComparator<Scalar> comparator1 = UniversalOrder.comparator(Scalars::lessEquals);
    List<UniversalComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    // FIXME ASTOLL warnings
    UniversalLexicographicOrder<Scalar> lexicographicOrder = new UniversalLexicographicOrder(comparatorList);
    List<Scalar> x = new LinkedList<>();
    x.add(RealScalar.of(1));
    x.add(RealScalar.of(2));
    x.add(RealScalar.of(2));
    List<Scalar> y = new LinkedList<>();
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(2));
    assertEquals(UniversalComparison.STRICTLY_PRECEDES, lexicographicOrder.compare(x, y));
    assertEquals(UniversalComparison.STRICTLY_SUCCEDES, lexicographicOrder.compare(y, x));
    assertEquals(UniversalComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
    assertEquals(UniversalComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
  }

  public void testPartialLexicographic() {
    UniversalComparator<Scalar> comparator1 = UniversalOrder.comparator((x, y) -> Scalars.divides(x.abs(), y.abs()));
    List<UniversalComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    UniversalLexicographicOrder<Scalar> lexicographicOrder = new UniversalLexicographicOrder(comparatorList);
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
    assertEquals(UniversalComparison.STRICTLY_PRECEDES, lexicographicOrder.compare(x, y));
    assertEquals(UniversalComparison.STRICTLY_SUCCEDES, lexicographicOrder.compare(y, x));
    assertEquals(UniversalComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
    assertEquals(UniversalComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
    assertEquals(UniversalComparison.INCOMPARABLE, lexicographicOrder.compare(x, z));
    assertEquals(UniversalComparison.INCOMPARABLE, lexicographicOrder.compare(y, z));
    assertEquals(UniversalComparison.INCOMPARABLE, lexicographicOrder.compare(z, x));
    assertEquals(UniversalComparison.INCOMPARABLE, lexicographicOrder.compare(z, y));
  }

  @SuppressWarnings("rawtypes")
  public void testException() {
    UniversalComparator<Scalar> comparator1 = UniversalOrder.comparator((x, y) -> Scalars.divides(x.abs(), y.abs()));
    List<UniversalComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    UniversalLexicographicOrder<Scalar> lexicographicOrder = new UniversalLexicographicOrder(comparatorList);
    List<Scalar> x = new LinkedList<>();
    x.add(RealScalar.of(2));
    x.add(RealScalar.of(-5));
    x.add(RealScalar.of(2));
    List<Scalar> y = new LinkedList<>();
    y.add(RealScalar.of(6));
    y.add(RealScalar.of(2));
    assertEquals(UniversalComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
    assertEquals(UniversalComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
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
  // public void testQualifiesForComparison() {
  // // TODO ASTOLL
  //
  // }
  // FIXME ASTOLL Does not work for comparator of different types
  // public void testMultipleDifferentComparators() {
  // UniversalComparator<Integer> comparator1 = UniversalOrder.comparator((x,y)-> x<=y);
  // UniversalComparator<Scalar> comparator2 = UniversalOrder.comparator(Scalars::lessEquals);
  // List<UniversalComparator<Object>> comparatorList = new LinkedList<>();
  // comparatorList.add(comparator1);
  // comparatorList.add(comparator2);
  // }
}
