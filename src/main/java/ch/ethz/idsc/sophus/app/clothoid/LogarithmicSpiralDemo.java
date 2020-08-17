// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import ch.ethz.idsc.sophus.crv.clothoid.LogarithmicSpiral;

/* package */ class LogarithmicSpiralDemo extends AbstractSpiralDemo {
  public LogarithmicSpiralDemo() {
    super(LogarithmicSpiral.of(1, 0.2));
  }

  public static void main(String[] args) {
    new LogarithmicSpiralDemo().setVisible(1000, 600);
  }
}
