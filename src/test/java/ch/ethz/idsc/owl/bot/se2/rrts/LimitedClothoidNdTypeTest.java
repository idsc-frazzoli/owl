// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.NdType;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.nd.NdCenterInterface;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LimitedClothoidNdTypeTest extends TestCase {
  public void testSimple() {
    Tensor center = Tensors.vector(2, 1, 0);
    NdType limitedClothoidNdType = LimitedClothoidNdType.with(1);
    NdCenterInterface ndCenterInterface = limitedClothoidNdType.ndCenterTo(center);
    Scalar scalar = ndCenterInterface.distance(Tensors.vector(0, 1, 0));
    assertEquals(scalar, RealScalar.of(2));
  }

  public void testQuantity() {
    Tensor center = Tensors.fromString("{0[m], 2[m], 0}");
    NdType limitedClothoidNdType = LimitedClothoidNdType.with(Quantity.of(1, "m^-1"));
    NdCenterInterface ndCenterInterface = limitedClothoidNdType.ndCenterFrom(center);
    assertEquals(ndCenterInterface.distance(Tensors.fromString("{2[m], 2[m], 0}")), Quantity.of(2, "m"));
    assertEquals(ndCenterInterface.distance(Tensors.fromString("{0[m], 2.1[m], 0}")), Quantity.of(Double.POSITIVE_INFINITY, "m"));
  }
}
