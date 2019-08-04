// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import java.util.Optional;

import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2ExpFixpointTest extends TestCase {
  public void testSimple() {
    Tensor velocity = Tensors.fromString("{3[m*s^-1], .2[m*s^-1], 0.3[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity);
    for (Tensor _t : Subdivide.of(Quantity.of(-2.1, "s"), Quantity.of(10, "s"), 13)) {
      Se2Bijection se2Bijection = new Se2Bijection(Se2CoveringExponential.INSTANCE.exp(velocity.multiply(_t.Get())));
      Chop._10.requireClose(se2Bijection.forward().apply(optional.get()), optional.get());
    }
  }

  public void testSimple2() {
    Tensor velocity = Tensors.fromString("{-3[m*s^-1], 1.2[m*s^-1], -0.3[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity);
    for (Tensor _t : Subdivide.of(Quantity.of(-5.1, "s"), Quantity.of(10, "s"), 17)) {
      Se2Bijection se2Bijection = new Se2Bijection(Se2CoveringExponential.INSTANCE.exp(velocity.multiply(_t.Get())));
      Chop._10.requireClose(se2Bijection.forward().apply(optional.get()), optional.get());
    }
  }

  public void testEmpty() {
    Tensor velocity = Tensors.fromString("{-3[m*s^-1], 1.2[m*s^-1], -0[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity);
    assertFalse(optional.isPresent());
  }

  public void testEmptyChop() {
    Tensor velocity = Tensors.fromString("{-3[m*s^-1], 1.2[m*s^-1], -0.00003[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity, Chop._03);
    assertFalse(optional.isPresent());
  }
}
