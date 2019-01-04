// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.dubins.DubinsPath;
import ch.ethz.idsc.sophus.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.dubins.DubinsPathGenerator;
import ch.ethz.idsc.sophus.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/* package */ class DubinsPathDemo extends AbstractDemo {
  private static final Tensor START = Tensors.vector(0, 0, 0);
  private static final Tensor ARROWHEAD = Arrowhead.of(0.5);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    final Tensor mouse = geometricLayer.getMouseSe2State();
    {
      graphics.setColor(Color.GREEN);
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(mouse));
      graphics.fill(geometricLayer.toPath2D(ARROWHEAD));
      geometricLayer.popMatrix();
    }
    // ---
    DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(START, mouse, RealScalar.of(1));
    graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
    graphics.setStroke(new BasicStroke(1f));
    for (DubinsPath dubinsPath : dubinsPathGenerator.allValid().collect(Collectors.toList()))
      graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    {
      graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
      graphics.setStroke(new BasicStroke(2f));
      DubinsPath dubinsPath = dubinsPathGenerator.allValid().min(DubinsPathComparator.length()).get();
      graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
    {
      graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
      graphics.setStroke(new BasicStroke(2f));
      DubinsPath dubinsPath = dubinsPathGenerator.allValid().min(DubinsPathComparator.curvature()).get();
      graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
  }

  private static Tensor sample(DubinsPath dubinsPath) {
    return Subdivide.of(RealScalar.ZERO, dubinsPath.length(), 200).map(dubinsPath.sampler(START));
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new DubinsPathDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
