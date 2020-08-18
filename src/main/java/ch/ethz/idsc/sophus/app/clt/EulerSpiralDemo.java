// code by jph
package ch.ethz.idsc.sophus.app.clt;

import ch.ethz.idsc.sophus.crv.EulerSpiral;

/* package */ class EulerSpiralDemo extends AbstractSpiralDemo {
  public EulerSpiralDemo() {
    super(EulerSpiral.FUNCTION);
  }

  public static void main(String[] args) {
    new EulerSpiralDemo().setVisible(1000, 600);
  }
}
