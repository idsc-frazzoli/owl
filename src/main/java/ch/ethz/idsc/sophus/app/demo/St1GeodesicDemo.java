// code by jph
package ch.ethz.idsc.sophus.app.demo;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.group.St1CoveringGeodesic;
import ch.ethz.idsc.sophus.group.St1Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.CirclePoints;



/* package */ class St1GeodesicDemo extends AbstractDemo {
  private static final Tensor FIRST = Tensors.vector(1,1);
  private static final Tensor CIRCLE = CirclePoints.of(20).multiply(RealScalar.of(.03));

  public St1GeodesicDemo() {
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor q = geometricLayer.getMouseSe2State().extract(0, 2);
    graphics.setColor(new Color(128, 128, 128, 128));
    for (Tensor scalar : Subdivide.of(0, 1, 20)) {
      Tensor split = St1Geodesic.INSTANCE.split(FIRST, q, scalar.Get());
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(split));
      graphics.fill(geometricLayer.toPath2D(CIRCLE));
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    St1GeodesicDemo geodesicDemo = new St1GeodesicDemo();
    geodesicDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    geodesicDemo.timerFrame.jFrame.setVisible(true);
  }
}
