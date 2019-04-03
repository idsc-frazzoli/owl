// code by jph
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ConvergenceTest extends TestCase {
  public void testSimple() {
    Tensor traj = //
        Tensors.vector(i -> Tensors.of(Quantity.of(1, "m"), Quantity.of(i, "m"), Pi.HALF), 20);
    System.out.println(Pretty.of(traj));
    Tensor pose = Tensors.fromString("{0.2[m],2.2[m],1}");
    // TODO MPC apply PID to initial condition above
  }
}
