// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Se2CurveConverterTest extends TestCase {
  public void testBase() {
    Tensor trajMeter = //
        Tensors.vector(i -> Tensors.of(Quantity.of(1, "m"), Quantity.of(i, "m"), Pi.HALF), 20);
    Tensor traj = //
        Tensors.vector(i -> Tensors.of(RealScalar.of(1), RealScalar.of(i), Pi.HALF), 20);
    Tensor trajConv = new Se2CurveConverter().toSI(traj);
    assertEquals(trajMeter, trajConv);
  }

  public void testSingleton() {
    Tensor poseMeter = Tensors.fromString("{6.2[m],4.2[m],1}");
    Tensor pose = Tensors.of(Tensors.fromString("{6.2, 4.2, 1}"));
    Tensor poseConv = new Se2CurveConverter().toSI(pose).get(0);
    assertEquals(poseMeter, poseConv);
  }
}
