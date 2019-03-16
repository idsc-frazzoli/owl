// code by jph
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GenericLexicographicComparatorTest extends TestCase {
  public void testSimple() {
    List<Scalar> list = new LinkedList<>();
    list.add(RealScalar.ONE);
    list.add(RealScalar.of(3));
    List<UniversalComparator> comparators = Arrays.asList( //
        UniversalOrder.comparator(Scalars::lessEquals), //
        UniversalOrder.comparator(Scalars::lessEquals)); //
    GenericLexicographicComparator genericLexicographicOrder = new GenericLexicographicComparator(comparators);
    UniversalComparison universalComparison = genericLexicographicOrder.compare(list, list);
    assertEquals(universalComparison, UniversalComparison.INDIFFERENT);
  }

  public void testMixed() {
    List<Object> listX = new LinkedList<>();
    listX.add(123);
    listX.add(Arrays.asList(2, 3, 4));
    List<Object> listY = new LinkedList<>();
    listY.add(123);
    listY.add(Arrays.asList(3, 4));
    // Object object = list.get(3);
    BinaryRelation<Integer> relation1 = (x, y) -> x < y;
    BinaryRelation<Collection<?>> relation2 = (x, y) -> y.containsAll(x);
    List<UniversalComparator> comparators = Arrays.asList( //
        UniversalOrder.comparator(relation1), //
        UniversalOrder.comparator(relation2)); //
    // UniversalOrder.comparator(binaryRelation);
    GenericLexicographicComparator genericLexicographicOrder = new GenericLexicographicComparator(comparators);
    UniversalComparison universalComparison = genericLexicographicOrder.compare(listX, listY);
    // System.out.println(universalComparison);
    assertEquals(universalComparison, UniversalComparison.STRICTLY_SUCCEDES);
  }

  public void testTensor() {
    Tensor tensorX = Tensors.fromString("{{1,2,3}, 2}");
    Tensor tensorY = Tensors.fromString("{{2,3,4,5},-2}");
    BinaryRelation<Tensor> relation1 = (x, y) -> x.length() <= y.length();
    List<UniversalComparator> comparators = Arrays.asList( //
        UniversalOrder.comparator(relation1), //
        UniversalOrder.comparator(Scalars::lessEquals)); //
    GenericLexicographicComparator genericLexicographicOrder = new GenericLexicographicComparator(comparators);
    UniversalComparison universalComparison = genericLexicographicOrder.compare(tensorX, tensorY);
    assertEquals(universalComparison, UniversalComparison.STRICTLY_PRECEDES);
  }
}
