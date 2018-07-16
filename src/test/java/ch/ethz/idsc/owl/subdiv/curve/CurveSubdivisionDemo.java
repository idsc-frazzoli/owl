// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Nest;

enum CurveSubdivisionDemo {
  ;
  static final Tensor ARROWHEAD = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(2));

  public static void main(String[] args) {
    TimerFrame timerFrame = new TimerFrame();
    timerFrame.jFrame.setBounds(100, 100, 600, 600);
    timerFrame.jFrame.setVisible(true);
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        Tensor q = geometricLayer.getMouseSe2State();
        graphics.setColor(new Color(128, 128, 128, 128));
        Tensor curve = Tensors.of(Array.zeros(3), Tensors.vector(3, 3, Math.PI / 2), q);
        CurveSubdivision curveSubdivision = new FourPointCurveSubdivision(Se2Geodesic.INSTANCE);
        curveSubdivision = new BSpline3CurveSubdivision(Se2Geodesic.INSTANCE);
        Tensor result = Nest.of(curveSubdivision::string, curve, 4);
        for (Tensor split : result) {
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(split));
          Path2D path2d = geometricLayer.toPath2D(ARROWHEAD);
          graphics.fill(path2d);
          geometricLayer.popMatrix();
        }
      }
    });
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
  }
}
