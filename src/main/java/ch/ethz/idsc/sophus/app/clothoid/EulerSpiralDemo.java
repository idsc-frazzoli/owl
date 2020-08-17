// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import ch.ethz.idsc.sophus.crv.clothoid.EulerSpiral;

/* package */ class EulerSpiralDemo extends AbstractSpiralDemo {
  public EulerSpiralDemo() {
    super(EulerSpiral.FUNCTION);
  }

  public static void main(String[] args) {
    new EulerSpiralDemo().setVisible(1000, 600);
  }
}
