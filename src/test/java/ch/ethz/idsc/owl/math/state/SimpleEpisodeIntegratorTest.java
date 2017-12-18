// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;

import ch.ethz.idsc.owl.bot.rice.Duncan1StateSpaceModel;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SimpleEpisodeIntegratorTest extends TestCase {
  public void testSimple() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor x = Tensors.vector(1, 2);
    Tensor u = Tensors.vector(5, -2);
    Scalar t = RealScalar.of(3);
    Scalar p = RealScalar.of(2);
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
      Flow flow = StateSpaceModels.createFlow(stateSpaceModel, u);
      List<StateTime> list = aei.move(flow, p);
      assertEquals(list.size(), 1);
      Tensor cmp = x.add(u.multiply(p));
      assertEquals(list.get(0).state(), cmp);
      assertEquals(list.get(0).time(), t.add(p));
    }
  }

  public void testUnits1() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor x = Tensors.fromString("{1[m], 2[m]}");
    Tensor u = Tensors.fromString("{5[m], -2[m]}");
    Scalar t = RealScalar.of(3);
    Scalar p = RealScalar.of(2);
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
      Flow flow = StateSpaceModels.createFlow(stateSpaceModel, u);
      List<StateTime> list = aei.move(flow, p);
      assertEquals(list.size(), 1);
      Tensor cmp = x.add(u.multiply(p));
      assertEquals(list.get(0).state(), cmp);
      assertEquals(list.get(0).time(), t.add(p));
    }
  }

  public void testUnits2() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor x = Tensors.fromString("{1[m], 2[m]}");
    Tensor u = Tensors.fromString("{5[m*s^-1], -2[m*s^-1]}");
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
      Flow flow = StateSpaceModels.createFlow(stateSpaceModel, u);
      List<StateTime> list = aei.move(flow, p);
      assertEquals(list.size(), 1);
      Tensor cmp = x.add(u.multiply(p));
      assertEquals(list.get(0).state(), cmp);
      assertEquals(list.get(0).time(), t.add(p));
    }
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
      Flow flow = StateSpaceModels.createFlow(stateSpaceModel, u);
      List<StateTime> list = aei.move(flow, p);
      assertEquals(list.size(), 1);
      assertEquals(list.get(0).time(), t.add(p));
    }
  }
}
