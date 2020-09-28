// code by jph
package ch.ethz.idsc.sophus.app.clt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.clt.ClothoidSolutions.Search;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidComparators;
import ch.ethz.idsc.sophus.clt.ClothoidContext;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.sca.Clips;

/** The demo shows that when using LaneRiesenfeldCurveSubdivision(Clothoid.INSTANCE, degree)
 * in order to connect two points p and q, then the (odd) degree has little influence on the
 * resulting curve. The difference is only noticeable for S shaped curves.
 * 
 * Therefore, for simplicity in algorithms we use degree == 1. */
/* package */ class ClothoidEmitDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 100);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = //
      ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ClothoidSolutions CLOTHOID_SOLUTIONS = ClothoidSolutions.of(Clips.absolute(15.0), 101);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    ClothoidContext clothoidContext = new ClothoidContext(START, mouse);
    Search search = CLOTHOID_SOLUTIONS.new Search(clothoidContext.s1(), clothoidContext.s2());
    List<Clothoid> list = ClothoidEmit.stream(clothoidContext, search.lambdas()).collect(Collectors.toList());
    int index = 0;
    for (Clothoid clothoid : list)
      new PathRender(COLOR_DATA_INDEXED.getColor(index++), 1.5f) //
          .setCurve(DOMAIN.map(clothoid), false).render(geometricLayer, graphics);
    // ---
    Optional<Clothoid> optional = list.stream().min(ClothoidComparators.CURVATURE_HEAD);
    if (optional.isPresent()) {
      Clothoid clothoid = optional.get();
      new PathRender(Color.BLACK, 2.5f) //
          .setCurve(DOMAIN.map(clothoid), false).render(geometricLayer, graphics);
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new ClothoidEmitDemo().setVisible(1000, 600);
  }
}
