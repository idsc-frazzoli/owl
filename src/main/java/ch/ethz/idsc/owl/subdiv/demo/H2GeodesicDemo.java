// code by jph and ob
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.H2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.Sign;

enum H2GeodesicDemo {
  ;
  private static final Tensor CIRCLE = CirclePoints.of(20).multiply(RealScalar.of(.03));

  public static void main(String[] args) {
    TimerFrame timerFrame = new TimerFrame();
    timerFrame.jFrame.setBounds(100, 100, 600, 600);
    timerFrame.jFrame.setVisible(true);
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        Tensor q = geometricLayer.getMouseSe2State().extract(0, 2);
        if (Sign.isPositive(q.Get(1))) {
          graphics.setColor(new Color(128, 128, 128, 128));
          for (Tensor scalar : Subdivide.of(0, 1, 40)) {
            Tensor split = H2Geodesic.INSTANCE.split(Tensors.vector(0, 1), q, scalar.Get());
            // split = RnGeodesic.INSTANCE.split(Array.zeros(3), q, scalar.Get());
            geometricLayer.pushMatrix(Se2Utils.toSE2Translation(split));
            Path2D path2d = geometricLayer.toPath2D(CIRCLE);
            graphics.fill(path2d);
            geometricLayer.popMatrix();
          }
        }
      }
    });
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
  }
}
