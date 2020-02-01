// code by jph
package ch.ethz.idsc.owl.bot.se2;

enum Se2IntegratorDemo {
  ;
  /** Example output:
   * se2_int 0.032712877
   * runge4_ 0.108451661
   * runge45 0.37093125200000004 */
  // FIXME
  // public static void main(String[] args) {
  // Flow flow = Se2CarFlows.singleton(RealScalar.ONE, RealScalar.ONE);
  // Timing s1 = Timing.stopped();
  // Timing s4 = Timing.stopped();
  // Timing s2 = Timing.stopped();
  // Timing s3 = Timing.stopped();
  // for (int count = 0; count < 10_000; ++count) {
  // Tensor x = RandomVariate.of(NormalDistribution.standard(), 3);
  // Scalar h = RandomVariate.of(NormalDistribution.standard());
  // s1.start();
  // Se2CarIntegrator.INSTANCE.step(flow, x, h);
  // s1.stop();
  // s4.start();
  // Se2FlowIntegrator.INSTANCE.step(flow, x, h);
  // s4.stop();
  // s2.start();
  // RungeKutta4Integrator.INSTANCE.step(flow, x, h);
  // s2.stop();
  // s3.start();
  // RungeKutta45Integrator.INSTANCE.step(flow, x, h);
  // s3.stop();
  // }
  // System.out.println("car_int " + s1.seconds());
  // System.out.println("se2_int " + s4.seconds());
  // System.out.println("runge4_ " + s2.seconds());
  // System.out.println("runge45 " + s3.seconds());
  // }
}
