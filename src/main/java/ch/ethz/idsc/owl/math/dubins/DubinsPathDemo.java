// code by jph
package ch.ethz.idsc.owl.math.dubins;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.owl.subdiv.demo.AbstractDemo;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ class DubinsPathDemo extends AbstractDemo {
  private static final Tensor ARROWHEAD = Arrowhead.of(.5);

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor mouse = Array.zeros(3);
    GraphicsUtil.setQualityHigh(graphics);
    mouse = geometricLayer.getMouseSe2State();
    {
      graphics.setColor(Color.GREEN);
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
      graphics.fill(geometricLayer.toPath2D(ARROWHEAD));
      geometricLayer.popMatrix();
    }
    // ---
    FixedRadiusDubins fixedRadiusDubins = new FixedRadiusDubins(mouse, RealScalar.of(1));
    for (DubinsPath dubinsPath : fixedRadiusDubins.allValid().collect(Collectors.toList())) {
      graphics.setColor(Color.BLUE);
      Path2D path2d = geometricLayer.toPath2D(sample(dubinsPath));
      graphics.draw(path2d);
    }
    {
      DubinsPath dubinsPath = fixedRadiusDubins.allValid().min(DubinsPathLengthComparator.INSTANCE).get();
      graphics.setColor(Color.RED);
      Path2D path2d = geometricLayer.toPath2D(sample(dubinsPath));
      graphics.setStroke(new BasicStroke(1.25f));
      graphics.draw(path2d);
      graphics.setStroke(new BasicStroke(1f));
    }
  }

  private static Tensor sample(DubinsPath dubinsPath) {
    return Subdivide.of(RealScalar.ZERO, dubinsPath.length(), 200) //
        .map(dubinsPath.sampler(Array.zeros(3)));
  }

  public static void main(String[] args) {
    DubinsPathDemo dubinsPathDemo = new DubinsPathDemo();
    dubinsPathDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    dubinsPathDemo.timerFrame.jFrame.setVisible(true);
  }
}
