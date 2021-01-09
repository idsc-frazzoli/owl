// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;

import ch.ethz.idsc.owl.bot.rice.Duncan1StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AbstractEpisodeIntegratorTest extends TestCase {
  public void testSmall() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    Scalar now = RealScalar.of(3.00000001);
    episodeIntegrator.move(Tensors.vector(1, 1), now);
    StateTime stateTime = episodeIntegrator.tail();
    assertEquals(stateTime.time(), now);
    assertFalse(init.equals(stateTime));
    Chop._04.requireClose(init.state(), stateTime.state());
  }

  public void testLarge() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2, 3), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        Se2StateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    Scalar now = RealScalar.of(3.3);
    episodeIntegrator.move(Tensors.vector(1, 0, 1), now);
    StateTime stateTime = episodeIntegrator.tail();
    assertEquals(stateTime.time(), now);
    assertFalse(init.equals(stateTime));
    Chop._13.requireClose(stateTime.state(), Tensors.vector(0.7011342979097925, 1.9974872733093685, 3.3));
  }

  public void testFail() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        Se2StateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    AssertFail.of(() -> episodeIntegrator.move(Tensors.vector(1), RealScalar.of(3)));
  }

  public void testRice1Units() {
    StateSpaceModel stateSpaceModel = new Duncan1StateSpaceModel(Quantity.of(3, "s^-1"));
    Tensor x = Tensors.fromString("{1[m*s^-1], 2[m*s^-1]}");
    Tensor u = Tensors.fromString("{5[m*s^-2], -2[m*s^-2]}");
    Scalar t = Scalars.fromString("3[s]");
    Scalar p = Scalars.fromString("2[s]");
    Integrator[] ints = new Integrator[] { //
        EulerIntegrator.INSTANCE, //
        MidpointIntegrator.INSTANCE, //
        RungeKutta4Integrator.INSTANCE, //
        RungeKutta45Integrator.INSTANCE //
    };
    for (Integrator integrator : ints) {
      AbstractEpisodeIntegrator aei = new SimpleEpisodeIntegrator( //
          stateSpaceModel, //
          integrator, new StateTime(x, t));
      Tensor flow = u;
      List<StateTime> list = aei.abstract_move(flow, p);
      assertEquals(list.size(), 1);
      assertEquals(list.get(0).time(), t.add(p));
    }
  }
}
