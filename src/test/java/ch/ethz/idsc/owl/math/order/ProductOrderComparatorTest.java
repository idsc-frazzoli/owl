// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.owl.demo.order.EqualityOrder;
import ch.ethz.idsc.owl.demo.order.IntegerTotalOrder;
import ch.ethz.idsc.owl.demo.order.ScalarTotalOrder;
import ch.ethz.idsc.owl.demo.order.SetPartialOrder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ProductOrderComparatorTest extends TestCase {
  public void testSimple() {
    List<OrderComparator> comparators = Arrays.asList( //
        ScalarTotalOrder.INSTANCE, //
        ScalarTotalOrder.INSTANCE); //
    ProductOrderComparator productOrderComparator = new ProductOrderComparator(comparators);
    List<Scalar> list = Arrays.asList(RealScalar.ONE, RealScalar.of(3));
    OrderComparison orderComparison = productOrderComparator.compare(list, list);
    assertEquals(orderComparison, OrderComparison.INDIFFERENT);
  }

  public void testMixed() {
    List<OrderComparator> comparators = Arrays.asList( //
        IntegerTotalOrder.INSTANCE, //
        SetPartialOrder.INSTANCE, //
        EqualityOrder.INSTANCE); //
    ProductOrderComparator productOrderComparator = new ProductOrderComparator(comparators);
    List<Object> listX = Arrays.asList(123, Arrays.asList(2, 3, 4), "abc");
    List<Object> listY = Arrays.asList(123, Arrays.asList(3, 4), "abc");
    List<Object> listZ = Arrays.asList(123, Arrays.asList(3, 4), "different");
    assertEquals(productOrderComparator.compare(listX, listY), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(productOrderComparator.compare(listY, listX), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(productOrderComparator.compare(listX, listX), OrderComparison.INDIFFERENT);
    assertEquals(productOrderComparator.compare(listY, listY), OrderComparison.INDIFFERENT);
    assertEquals(productOrderComparator.compare(listX, listZ), OrderComparison.INCOMPARABLE);
    assertEquals(productOrderComparator.compare(listY, listZ), OrderComparison.INCOMPARABLE);
    assertEquals(productOrderComparator.compare(listZ, listX), OrderComparison.INCOMPARABLE);
    assertEquals(productOrderComparator.compare(listZ, listY), OrderComparison.INCOMPARABLE);
    assertEquals(productOrderComparator.compare(listZ, listZ), OrderComparison.INDIFFERENT);
  }

  public void testTensor() {
    BinaryRelation<Tensor> relation1 = (x, y) -> x.length() <= y.length();
    List<OrderComparator> comparators = Arrays.asList( //
        new Order<>(relation1), //
        ScalarTotalOrder.INSTANCE); //
    ProductOrderComparator genericProductOrder = new ProductOrderComparator(comparators);
    Tensor tensorX = Tensors.fromString("{{1,2,3}, 10}");
    Tensor tensorY = Tensors.fromString("{{2,3,4,5},7}");
    OrderComparison orderComparison = genericProductOrder.compare(tensorX, tensorY);
    assertEquals(orderComparison, OrderComparison.INCOMPARABLE);
  }
}
