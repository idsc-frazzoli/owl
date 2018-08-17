// code by jph
package ch.ethz.idsc.owl.math.dubins;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;

class DubinsPathDemo {
  private static final Tensor ARROWHEAD_HI = Tensors.matrixDouble( //
      new double[][] { { .3, 0 }, { -.1, -.1 }, { -.1, +.1 } }).multiply(RealScalar.of(1.2));
  // ---
  private final TimerFrame timerFrame = new TimerFrame();
  private Tensor mouse = Array.zeros(3);

  DubinsPathDemo() {
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        GraphicsUtil.setQualityHigh(graphics);
        mouse = geometricLayer.getMouseSe2State();
        {
          graphics.setColor(Color.GREEN);
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
          graphics.fill(geometricLayer.toPath2D(ARROWHEAD_HI));
          geometricLayer.popMatrix();
        }
        // ---
        FixedRadiusDubins fixedRadiusDubins = new FixedRadiusDubins(mouse, RealScalar.of(1));
        for (DubinsPath dubinsPath : fixedRadiusDubins.allValid().collect(Collectors.toList())) {
          Scalar length = dubinsPath.length();
          Tensor tensor = Tensors.empty();
          for (Tensor _at : Subdivide.of(RealScalar.ZERO, length, 200)) {
            Scalar lambda = (Scalar) _at;
            Tensor poseAt = dubinsPath.getPoseAt(Array.zeros(3), lambda);
            tensor.append(poseAt);
          }
          {
            graphics.setColor(Color.BLUE);
            Path2D path2d = geometricLayer.toPath2D(tensor);
            graphics.setStroke(new BasicStroke(1.25f));
            graphics.draw(path2d);
            graphics.setStroke(new BasicStroke(1f));
          }
        }
      }
    });
  }

  public static void main(String[] args) {
    DubinsPathDemo dubinsPathDemo = new DubinsPathDemo();
    dubinsPathDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    dubinsPathDemo.timerFrame.jFrame.setVisible(true);
  }
}
