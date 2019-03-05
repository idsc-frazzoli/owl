// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.api.Se2CoveringGeodesicDisplay;
import ch.ethz.idsc.sophus.curve.BSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.curve.ClothoidCurve;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.dubins.DubinsPath;
import ch.ethz.idsc.sophus.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.dubins.DubinsPathGenerator;
import ch.ethz.idsc.sophus.dubins.FixedRadiusDubins;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.PadLeft;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.red.Nest;

public class DubinsPathDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final int POINTS = 200;
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  // ---
  private final PathRender pathRender = new PathRender(Color.RED, 2f);
  private final PathRender pathRender2 = new PathRender(Color.CYAN, 2f);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(START, mouse, RealScalar.of(1));
    List<DubinsPath> list = dubinsPathGenerator.allValid().collect(Collectors.toList());
    {
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.setStroke(new BasicStroke(1f));
      for (DubinsPath dubinsPath : list)
        graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
    { // draw shortest path
      graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
      graphics.setStroke(new BasicStroke(2f));
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparator.length()).get();
      graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
    {
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparator.length()).get();
      ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(START);
      Tensor params = PadLeft.zeros(4).apply(dubinsPath.segments());
      graphics.setColor(new Color(128, 128, 128, 128));
      // graphics.setColor(COLOR_DATA_INDEXED.getColor(3));
      Tensor map = params.map(scalarTensorFunction);
      for (Tensor point : map) {
        geometricLayer.pushMatrix(Se2CoveringGeodesicDisplay.INSTANCE.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(Se2CoveringGeodesicDisplay.INSTANCE.shape());
        graphics.fill(path2d);
        geometricLayer.popMatrix();
      }
      BSpline3CurveSubdivision bSpline3CurveSubdivision = //
          new BSpline3CurveSubdivision(Se2CoveringGeodesic.INSTANCE);
      Tensor points = Nest.of(bSpline3CurveSubdivision::string, map, 5);
      // graphics.setStroke(new BasicStroke(2f));
      pathRender.setCurve(points, false).render(geometricLayer, graphics);
    }
    {
      CurveSubdivision curveSubdivision = LaneRiesenfeldCurveSubdivision.numeric(ClothoidCurve.INSTANCE, 1);
      Tensor points = Nest.of(curveSubdivision::string, Tensors.of(START, mouse), 6);
      pathRender2.setCurve(points, false).render(geometricLayer, graphics);
    }
    { // draw least curved path
      graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
      graphics.setStroke(new BasicStroke(2f));
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparator.curvature()).get();
      graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
  }

  private static Tensor sample(DubinsPath dubinsPath) {
    return Subdivide.of(RealScalar.ZERO, dubinsPath.length(), POINTS).map(dubinsPath.sampler(START));
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new DubinsPathDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
