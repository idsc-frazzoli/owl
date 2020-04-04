// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathGenerator;
import ch.ethz.idsc.sophus.crv.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

public class DubinsTransitionDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();

  public DubinsTransitionDemo() {
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(START, mouse, RealScalar.of(1));
    List<DubinsPath> list = dubinsPathGenerator.allValid().collect(Collectors.toList());
    Scalar minResolution = RealScalar.of(geometricLayer.pixel2modelWidth(5));
    {
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.setStroke(new BasicStroke(1));
      for (DubinsPath dubinsPath : list) {
        DubinsTransition dubinsTransition = new DubinsTransition(START, mouse, dubinsPath);
        graphics.draw(geometricLayer.toPath2D(dubinsTransition.linearized(minResolution)));
      }
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new DubinsTransitionDemo().setVisible(1000, 600);
  }
}
