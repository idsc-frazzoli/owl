// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.owl.math.map.Se2Integrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

enum Se2IntegratorDemo {
  ;
  /** Example output:
   * se2_int 0.032712877
   * runge4_ 0.108451661
   * runge45 0.37093125200000004 */
  public static void main(String[] args) {
    Flow flow = CarFlows.singleton(RealScalar.ONE, RealScalar.ONE);
    Stopwatch s1 = Stopwatch.stopped();
    Stopwatch s4 = Stopwatch.stopped();
    Stopwatch s2 = Stopwatch.stopped();
    Stopwatch s3 = Stopwatch.stopped();
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
    System.out.println("car_int " + s1.display_seconds());
    System.out.println("se2_int " + s4.display_seconds());
    System.out.println("runge4_ " + s2.display_seconds());
    System.out.println("runge45 " + s3.display_seconds());
  }
}
