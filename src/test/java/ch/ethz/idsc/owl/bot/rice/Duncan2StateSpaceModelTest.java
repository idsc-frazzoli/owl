// code by jph
package ch.ethz.idsc.owl.bot.rice;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Duncan2StateSpaceModelTest extends TestCase {
  public void testScalar() {
    StateSpaceModel stateSpaceModel = new Duncan2StateSpaceModel(Quantity.of(0.1, "s^-1"));
    Tensor x = Tensors.fromString("{10[m], 5[m*s^-1]}");
    Tensor u = Tensors.fromString("{-1[m*s^-2]}");
    Tensor x_fxu = x.add(stateSpaceModel.f(x, u).multiply(Quantity.of(1, "s")));
    assertEquals(x_fxu, Tensors.fromString("{15[m], 3.5[m*s^-1]}"));
  }

  public void testZero() {
    StateSpaceModel stateSpaceModel = new Duncan2StateSpaceModel(Quantity.of(0, "s^-1"));
    Tensor x = Tensors.fromString("{10[m], 5[m*s^-1]}");
    Tensor u = Tensors.fromString("{-1[m*s^-2]}");
    Tensor x_fxu = x.add(stateSpaceModel.f(x, u).multiply(Quantity.of(1, "s")));
    assertEquals(x_fxu, Tensors.fromString("{15[m], 4[m*s^-1]}"));
  }

  public void testLipschitz() {
  }

  public void testFail() {
    try {
      new Duncan2StateSpaceModel(Quantity.of(-1.0, "s^-1"));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
