package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class Se2CurveConverterTest extends TestCase {
  public void testBase() {
    Tensor trajMeter = //
        Tensors.vector(i -> Tensors.of(Quantity.of(1, "m"), Quantity.of(i, "m"), Pi.HALF), 20);
    System.out.println(Pretty.of(trajMeter));
    Tensor traj = //
        Tensors.vector(i -> Tensors.of(RealScalar.of(1), RealScalar.of(i), Pi.HALF), 20);
    Tensor trajConv = Se2CurveConverter.toMeter(traj);
    System.out.println(Pretty.of(trajConv));
    boolean bool = (traj.toString() == trajConv.toString());
    System.out.println(traj.toString());
    System.out.println(trajConv.toString());
    // TODO JPH why difference in string ?
    // GlobalAssert.that(traj.equals(trajConv)); // this should normally be working
  }
}
