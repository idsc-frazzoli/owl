// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BoundedEpisodeIntegratorTest extends TestCase {
  public void testSimple() {
    BoundedEpisodeIntegrator boundedEpisodeIntegrator = new BoundedEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(1, 2), RealScalar.of(1)), //
        RealScalar.of(1));
    boundedEpisodeIntegrator.move(Tensors.vector(2, -1), RealScalar.of(3));
    StateTime stateTime = boundedEpisodeIntegrator.tail();
    assertEquals(stateTime.state(), Tensors.vector(5, 0));
    assertEquals(stateTime.time(), RealScalar.of(3));
    try {
      boundedEpisodeIntegrator.move(Tensors.vector(3, 4), RealScalar.of(3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
    try {
      boundedEpisodeIntegrator.move(Tensors.vector(3, 4), RealScalar.of(2.3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNegativeFail() {
    try {
      new BoundedEpisodeIntegrator( //
          SingleIntegratorStateSpaceModel.INSTANCE, //
          EulerIntegrator.INSTANCE, //
          new StateTime(Tensors.vector(1, 2), RealScalar.ZERO), //
          RealScalar.of(-1));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
