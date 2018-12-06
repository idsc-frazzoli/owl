// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.group.Se2CoveringGeodesic;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ class Se2GeodesicDemo extends AbstractDemo {
  private static final Tensor ARROWHEAD = Arrowhead.of(RealScalar.of(.4));

  public Se2GeodesicDemo() {
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor q = geometricLayer.getMouseSe2State();
    graphics.setColor(new Color(128, 128, 128, 128));
    for (Tensor scalar : Subdivide.of(0, 1, 20)) {
      Tensor split = Se2CoveringGeodesic.INSTANCE.split(Array.zeros(3), q, scalar.Get());
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(split));
      graphics.fill(geometricLayer.toPath2D(ARROWHEAD));
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    Se2GeodesicDemo geodesicDemo = new Se2GeodesicDemo();
    geodesicDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    geodesicDemo.timerFrame.jFrame.setVisible(true);
  }
}
