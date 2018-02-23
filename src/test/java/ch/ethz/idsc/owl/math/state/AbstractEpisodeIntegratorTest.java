// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.owl.bot.psu.PsuStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AbstractEpisodeIntegratorTest extends TestCase {
  public void testSmall() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    Scalar now = RealScalar.of(3.00000001);
    episodeIntegrator.move(Tensors.vector(1), now);
    StateTime stateTime = episodeIntegrator.tail();
    assertEquals(stateTime.time(), now);
    assertFalse(init.equals(stateTime));
    assertTrue(Chop._04.close(init.state(), stateTime.state()));
  }

  public void testLarge() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    Scalar now = RealScalar.of(3.3);
    episodeIntegrator.move(Tensors.vector(1), now);
    StateTime stateTime = episodeIntegrator.tail();
    assertEquals(stateTime.time(), now);
    assertFalse(init.equals(stateTime));
    assertTrue(Chop._13.close(stateTime.state(), Tensors.vector(1.6034722573306643, 2.015192617032934)));
  }

  public void testFail() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    try {
      episodeIntegrator.move(Tensors.vector(1), RealScalar.of(3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
