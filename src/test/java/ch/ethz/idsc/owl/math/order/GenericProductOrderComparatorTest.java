// code by astoll
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

public class GenericProductOrderComparatorTest extends TestCase {
  public void testSimple() {
    List<Scalar> list = new LinkedList<>();
    list.add(RealScalar.ONE);
    list.add(RealScalar.of(3));
    List<OrderComparator> comparators = Arrays.asList( //
        Order.comparator(Scalars::lessEquals), //
        Order.comparator(Scalars::lessEquals)); //
    GenericProductOrderComparator genericProductOrder = new GenericProductOrderComparator(comparators);
    OrderComparison orderComparison = genericProductOrder.compare(list, list);
    assertEquals(orderComparison, OrderComparison.INDIFFERENT);
  }

  public void testMixed() {
    List<Object> listX = new LinkedList<>();
    listX.add(123);
    listX.add(Arrays.asList(2, 3, 4));
    List<Object> listY = new LinkedList<>();
    listY.add(123);
    listY.add(Arrays.asList(3, 4));
    BinaryRelation<Integer> relation1 = (x, y) -> x <= y;
    BinaryRelation<Collection<?>> relation2 = (x, y) -> y.containsAll(x);
    List<OrderComparator> comparators = Arrays.asList( //
        Order.comparator(relation1), //
        Order.comparator(relation2)); //
    GenericProductOrderComparator genericProductOrder = new GenericProductOrderComparator(comparators);
    OrderComparison orderComparison = genericProductOrder.compare(listX, listY);
    assertEquals(orderComparison, OrderComparison.STRICTLY_SUCCEEDS);
  }

  public void testTensorSimple() {
    Tensor tensorX = Tensors.fromString("{1,2,3}");
    Tensor tensorY = Tensors.fromString("{2,3,4}");
    Tensor tensorZ = Tensors.fromString("{0,3,3}");
    List<OrderComparator> comparators = Arrays.asList( //
        Order.comparator(Scalars::lessEquals), Order.comparator(Scalars::lessEquals), //
        Order.comparator(Scalars::lessEquals)); //
    GenericProductOrderComparator genericProductOrder = new GenericProductOrderComparator(comparators);
    OrderComparison orderComparison1 = genericProductOrder.compare(tensorX, tensorY);
    OrderComparison orderComparison2 = genericProductOrder.compare(tensorY, tensorX);
    OrderComparison orderComparison3 = genericProductOrder.compare(tensorX, tensorZ);
    OrderComparison orderComparison4 = genericProductOrder.compare(tensorX, tensorX);
    assertEquals(orderComparison1, OrderComparison.STRICTLY_PRECEDES);
    assertEquals(orderComparison2, OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(orderComparison3, OrderComparison.INCOMPARABLE);
    assertEquals(orderComparison4, OrderComparison.INDIFFERENT);
  }
  
   public void testTensor() {
   Tensor tensorX = Tensors.fromString("{{1,2,3}, 10}");
   Tensor tensorY = Tensors.fromString("{{2,3,4,5},7}");
   BinaryRelation<Tensor> relation1 = (x, y) -> x.length() <= y.length();
   List<OrderComparator> comparators = Arrays.asList( //
   Order.comparator(relation1), //
   Order.comparator(Scalars::lessEquals)); //
   GenericProductOrderComparator genericProductOrder = new GenericProductOrderComparator(comparators);
   OrderComparison orderComparison = genericProductOrder.compare(tensorX, tensorY);
   assertEquals(orderComparison, OrderComparison.INCOMPARABLE);
   }
}
