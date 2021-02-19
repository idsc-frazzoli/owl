// code by jph
package ch.ethz.idsc.sophus.app.spi;

import ch.ethz.idsc.sophus.crv.se2c.LogarithmicSpiral;

/* package */ class LogarithmicSpiralDemo extends AbstractSpiralDemo {
  public LogarithmicSpiralDemo() {
    super(LogarithmicSpiral.of(1, 0.2));
  }

  public static void main(String[] args) {
    new LogarithmicSpiralDemo().setVisible(1000, 600);
  }
}
