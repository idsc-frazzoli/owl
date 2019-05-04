// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.demo.order.EqualityOrder;
import ch.ethz.idsc.owl.demo.order.IntegerTotalOrder;
import ch.ethz.idsc.owl.demo.order.SetPartialOrder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GenericProductOrderComparatorTest extends TestCase {
  public void testSimple() {
    List<OrderComparator> comparators = Arrays.asList( //
        Order.comparator(Scalars::lessEquals), //
        Order.comparator(Scalars::lessEquals)); //
    GenericProductOrderComparator genericProductOrder = new GenericProductOrderComparator(comparators);
    List<Scalar> list = Arrays.asList(RealScalar.ONE, RealScalar.of(3));
    OrderComparison orderComparison = genericProductOrder.compare(list, list);
    assertEquals(orderComparison, OrderComparison.INDIFFERENT);
  }

  public void testMixed() {
    List<OrderComparator> comparators = Arrays.asList( //
        IntegerTotalOrder.INSTANCE, //
        SetPartialOrder.INSTANCE, //
        EqualityOrder.INSTANCE); //
    GenericProductOrderComparator genericProductOrder = new GenericProductOrderComparator(comparators);
    List<Object> listX = Arrays.asList(123, Arrays.asList(2, 3, 4), "abc");
    List<Object> listY = Arrays.asList(123, Arrays.asList(3, 4), "abc");
    List<Object> listZ = Arrays.asList(123, Arrays.asList(3, 4), "different");
    assertEquals(genericProductOrder.compare(listX, listY), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(genericProductOrder.compare(listY, listX), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(genericProductOrder.compare(listX, listX), OrderComparison.INDIFFERENT);
    assertEquals(genericProductOrder.compare(listY, listY), OrderComparison.INDIFFERENT);
    assertEquals(genericProductOrder.compare(listX, listZ), OrderComparison.INCOMPARABLE);
    assertEquals(genericProductOrder.compare(listY, listZ), OrderComparison.INCOMPARABLE);
    assertEquals(genericProductOrder.compare(listZ, listX), OrderComparison.INCOMPARABLE);
    assertEquals(genericProductOrder.compare(listZ, listY), OrderComparison.INCOMPARABLE);
    assertEquals(genericProductOrder.compare(listZ, listZ), OrderComparison.INDIFFERENT);
  }

  public void testTensorSimple() {
    OrderComparator<Scalar> orderComparator = Order.comparator(Scalars::lessEquals);
    List<OrderComparator> comparators = Arrays.asList( //
        orderComparator, //
        orderComparator, //
        orderComparator); //
    GenericProductOrderComparator genericProductOrder = new GenericProductOrderComparator(comparators);
    Tensor tensorX = Tensors.fromString("{1,2,3}");
    Tensor tensorY = Tensors.fromString("{2,3,4}");
    Tensor tensorZ = Tensors.fromString("{0,3,3}");
    assertEquals(genericProductOrder.compare(tensorX, tensorY), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(genericProductOrder.compare(tensorY, tensorX), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(genericProductOrder.compare(tensorX, tensorZ), OrderComparison.INCOMPARABLE);
    assertEquals(genericProductOrder.compare(tensorZ, tensorX), OrderComparison.INCOMPARABLE);
    assertEquals(genericProductOrder.compare(tensorX, tensorX), OrderComparison.INDIFFERENT);
    assertEquals(genericProductOrder.compare(tensorZ, tensorZ), OrderComparison.INDIFFERENT);
  }

  public void testTensor() {
    BinaryRelation<Tensor> relation1 = (x, y) -> x.length() <= y.length();
    List<OrderComparator> comparators = Arrays.asList( //
        Order.comparator(relation1), //
        Order.comparator(Scalars::lessEquals)); //
    GenericProductOrderComparator genericProductOrder = new GenericProductOrderComparator(comparators);
    Tensor tensorX = Tensors.fromString("{{1,2,3}, 10}");
    Tensor tensorY = Tensors.fromString("{{2,3,4,5},7}");
    OrderComparison orderComparison = genericProductOrder.compare(tensorX, tensorY);
    assertEquals(orderComparison, OrderComparison.INCOMPARABLE);
  }

  public void testTotalProduct() {
    OrderComparator<Scalar> comparator1 = Order.comparator(Scalars::lessEquals);
    List<OrderComparator> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    GenericProductOrderComparator genericProductOrderComparator = new GenericProductOrderComparator(comparatorList);
    // FIXME ASTOLL warnings
    // ProductOrder<Scalar> productOrder = new ProductOrder<>(comparatorList);
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
    assertEquals(OrderComparison.STRICTLY_PRECEDES, genericProductOrderComparator.compare(x, y));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, genericProductOrderComparator.compare(y, x));
    // assertEquals(UniversalComparison.INDIFFERENT, productOrder.compare(y, y));
    // assertEquals(UniversalComparison.INDIFFERENT, productOrder.compare(x, x));
  }
}
