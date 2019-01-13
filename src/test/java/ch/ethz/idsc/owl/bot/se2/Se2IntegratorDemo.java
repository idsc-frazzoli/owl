// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.map.Se2Integrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

enum Se2IntegratorDemo {
  ;
  /** Example output:
   * se2_int 0.032712877
   * runge4_ 0.108451661
   * runge45 0.37093125200000004 */
  public static void main(String[] args) {
    Flow flow = CarHelper.singleton(RealScalar.ONE, RealScalar.ONE);
    Timing s1 = Timing.stopped();
    Timing s4 = Timing.stopped();
    Timing s2 = Timing.stopped();
    Timing s3 = Timing.stopped();
    for (int count = 0; count < 10000; ++count) {
      Tensor x = RandomVariate.of(NormalDistribution.standard(), 3);
      Scalar h = RandomVariate.of(NormalDistribution.standard());
      s1.start();
      Se2CarIntegrator.INSTANCE.step(flow, x, h);
      s1.stop();
      s4.start();
      Se2Integrator.INSTANCE.step(flow, x, h);
      s4.stop();
      s2.start();
      RungeKutta4Integrator.INSTANCE.step(flow, x, h);
      s2.stop();
      s3.start();
      RungeKutta45Integrator.INSTANCE.step(flow, x, h);
      s3.stop();
    }
    System.out.println("car_int " + s1.seconds());
    System.out.println("se2_int " + s4.seconds());
    System.out.println("runge4_ " + s2.seconds());
    System.out.println("runge45 " + s3.seconds());
  }
}
