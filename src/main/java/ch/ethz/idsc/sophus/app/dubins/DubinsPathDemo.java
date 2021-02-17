// code by jph
package ch.ethz.idsc.sophus.app.dubins;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransition;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparators;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathGenerator;
import ch.ethz.idsc.sophus.crv.dubins.FixedRadiusDubins;
import ch.ethz.idsc.sophus.gds.Se2CoveringDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.AbstractDemo;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.ref.d1.BSpline3CurveSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.PadLeft;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.red.Nest;

/* package */ class DubinsPathDemo extends AbstractDemo implements DemoInterface {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final int POINTS = 200;
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  // ---
  private final PathRender pathRender = new PathRender(Color.RED, 2f);
  private final PathRender pathRenderClothoid = new PathRender(Color.CYAN, 2f);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(START, mouse, RealScalar.of(1));
    List<DubinsPath> list = dubinsPathGenerator.stream().collect(Collectors.toList());
    {
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.setStroke(new BasicStroke(1f));
      for (DubinsPath dubinsPath : list)
        graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
    { // draw shortest path
      graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
      graphics.setStroke(new BasicStroke(2f));
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparators.LENGTH).get();
      graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
    {
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparators.LENGTH).get();
      ScalarTensorFunction scalarTensorFunction = dubinsPath.sampler(START);
      Tensor params = PadLeft.zeros(4).apply(dubinsPath.segments());
      graphics.setColor(new Color(128, 128, 128, 128));
      // graphics.setColor(COLOR_DATA_INDEXED.getColor(3));
      Tensor map = params.map(scalarTensorFunction);
      for (Tensor point : map) { // draw control point
        geometricLayer.pushMatrix(Se2CoveringDisplay.INSTANCE.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(Se2CoveringDisplay.INSTANCE.shape());
        graphics.fill(path2d);
        geometricLayer.popMatrix();
      }
      BSpline3CurveSubdivision bSpline3CurveSubdivision = //
          new BSpline3CurveSubdivision(Se2CoveringGeodesic.INSTANCE);
      Tensor points = Nest.of(bSpline3CurveSubdivision::string, map, 5);
      // graphics.setStroke(new BasicStroke(2f));
      pathRender.setCurve(points, false).render(geometricLayer, graphics);
    }
    { // draw clothoid
      ClothoidTransition clothoidTransition = //
          ClothoidTransition.of(CLOTHOID_BUILDER, START, mouse);
      Tensor tensor = clothoidTransition.linearized(RealScalar.of(0.1));
      pathRenderClothoid.setCurve(tensor, false).render(geometricLayer, graphics);
      // TODO
    }
    { // draw least curved path
      graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
      graphics.setStroke(new BasicStroke(2f));
      DubinsPath dubinsPath = list.stream().min(DubinsPathComparators.TOTAL_CURVATURE).get();
      graphics.draw(geometricLayer.toPath2D(sample(dubinsPath)));
    }
  }

  private static Tensor sample(DubinsPath dubinsPath) {
    return Subdivide.of(0.0, 1.0, POINTS).map(dubinsPath.unit(START));
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new DubinsPathDemo().setVisible(1000, 600);
  }
}
