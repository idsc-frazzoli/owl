// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class UniversalProductOrderTest extends TestCase {
  public void testTotalProduct() {
    UniversalComparator<Scalar> comparator1 = UniversalOrder.comparator(Scalars::lessEquals);
    List<UniversalComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    // FIXME ASTOLL warnings
    UniversalProductOrder<Scalar> productOrder = new UniversalProductOrder(comparatorList);
    List<Scalar> x = new LinkedList<>();
    x.add(RealScalar.of(1));
    x.add(RealScalar.of(2));
    x.add(RealScalar.of(2));
    List<Scalar> y = new LinkedList<>();
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(3));
    y.add(RealScalar.of(3));
    List<Scalar> z = new LinkedList<>();
    z.add(RealScalar.of(2));
    z.add(RealScalar.of(2));
    z.add(RealScalar.of(2));
    assertEquals(UniversalComparison.STRICTLY_PRECEDES, productOrder.compare(x, y));
    assertEquals(UniversalComparison.STRICTLY_SUCCEDES, productOrder.compare(y, x));
    // assertEquals(UniversalComparison.INDIFFERENT, productOrder.compare(y, y));
    // assertEquals(UniversalComparison.INDIFFERENT, productOrder.compare(x, x));
  }
}