// code by jph and ob
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.H2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class H2GeodesicDemo extends AbstractDemo {
  private static final Tensor CIRCLE = CirclePoints.of(20).multiply(RealScalar.of(.03));
  private static final Tensor FIRST = Tensors.vector(0, 1);
  private static final Color COLOR = new Color(128, 128, 128, 128);
  private static final int RESOLUTION = 40;
  // ---
  private Tensor q = Tensors.vector(1, 2);

  public H2GeodesicDemo() {
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor candidate = geometricLayer.getMouseSe2State().extract(0, 2);
    if (Sign.isPositive(candidate.Get(1)))
      q = candidate;
    graphics.setColor(COLOR);
    for (Tensor scalar : Subdivide.of(0, 1, RESOLUTION)) {
      Tensor split = H2Geodesic.INSTANCE.split(FIRST, q, scalar.Get());
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(split));
      graphics.fill(geometricLayer.toPath2D(CIRCLE));
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new H2GeodesicDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
