// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class FixedStateIntegratorTest extends TestCase {
  public void testSimple() {
    FixedStateIntegrator fsi = //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(1, 2), 3);
    Flow flow = StateSpaceModels.createFlow(SingleIntegratorStateSpaceModel.INSTANCE, Tensors.vector(1, 2));
    List<StateTime> list = fsi.trajectory(new StateTime(Tensors.vector(2, 3), RealScalar.of(10)), flow);
    assertEquals(list.size(), 3);
    assertEquals(list.get(2).time(), Scalars.fromString("10+3/2"));
    assertEquals(fsi.getTimeStepTrajectory(), RationalScalar.of(3, 2));
  }

  public void testCarEx() {
    Scalar dt = RationalScalar.of(1, 10);
    FixedStateIntegrator FIXEDSTATEINTEGRATOR = //
        FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, dt, 4);
    Scalar r = FIXEDSTATEINTEGRATOR.getTimeStepTrajectory();
    // System.out.println(r);
    assertEquals(r, dt.multiply(RealScalar.of(4)));
  }

  public void testFail1() {
    try {
      FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RealScalar.of(-.1), 3);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail2() {
    try {
      FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RealScalar.of(0), 3);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
