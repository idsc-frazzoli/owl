// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CarIntegratorTest extends TestCase {
  public void testStraight() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(2);
    Flow flow = CarHelper.singleton(RealScalar.ONE, RealScalar.ZERO);
    Se2StateSpaceModel.INSTANCE.f(x, flow.getU());
    Tensor expl = Se2CarIntegrator.INSTANCE.step(flow, x, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
    assertTrue(Chop._10.close(impl, expl));
  }

  public void testRotate1() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(.25);
    Flow flow = CarHelper.singleton(RealScalar.ONE, RealScalar.ONE);
    Se2StateSpaceModel.INSTANCE.f(x, flow.getU());
    Tensor expl = Se2CarIntegrator.INSTANCE.step(flow, x, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
    assertTrue(Chop._10.close(impl, expl));
  }

  public void testRotate2() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(.25);
    Flow flow = CarHelper.singleton(RealScalar.of(.5), RealScalar.of(2));
    Se2StateSpaceModel.INSTANCE.f(x, flow.getU());
    Tensor expl = Se2CarIntegrator.INSTANCE.step(flow, x, h);
    Tensor imp1 = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
    assertTrue(Chop._07.close(imp1, expl));
  }

  public void testRotateHN() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(-.25);
    Flow flow = CarHelper.singleton(RealScalar.of(.7), RealScalar.of(1.2));
    Se2StateSpaceModel.INSTANCE.f(x, flow.getU());
    Tensor expl = Se2CarIntegrator.INSTANCE.step(flow, x, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
    assertTrue(Chop._07.close(impl, expl));
  }

  public void testRotateUN() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(.25);
    Flow flow = CarHelper.singleton(RealScalar.of(-.8), RealScalar.of(2));
    Se2StateSpaceModel.INSTANCE.f(x, flow.getU());
    Tensor expl = Se2CarIntegrator.INSTANCE.step(flow, x, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
    assertTrue(Chop._07.close(impl, expl));
  }
}
