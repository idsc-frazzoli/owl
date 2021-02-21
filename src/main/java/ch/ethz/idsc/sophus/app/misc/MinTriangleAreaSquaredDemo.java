// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Graphics2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.gui.win.DubinsGenerator;
import ch.ethz.idsc.sophus.lie.r3.MinTriangleAreaSquared;
import ch.ethz.idsc.sophus.math.AppendOne;
import ch.ethz.idsc.sophus.ply.PolygonCentroid;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/** Reference:
 * "Polygon Laplacian Made Simple"
 * by Astrid Bunge, Philipp Herholz, Misha Kazhdan, Mario Botsch, 2020 */
/* package */ class MinTriangleAreaSquaredDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  // ---
  private final PathRender pathRender = new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f);

  public MinTriangleAreaSquaredDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterface(AxesRender.INSTANCE);
    timerFrame.geometricComponent.addRenderInterface(pathRender);
    // ---
    Tensor blub = Tensors.fromString("{{1, 0, 0}, {0, 1, 0}, {2, 0, 2.5708}, {1, 0, 2.1}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 0), //
        Tensor.of(blub.stream().map(Tensors.vector(2, 1, 1)::pmul))));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    final ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Tensor sequence = getGeodesicControlPoints();
    pathRender.setCurve(sequence, true);
    if (0 < sequence.length()) {
      Tensor polygon = Tensor.of(sequence.stream().map(AppendOne.FUNCTION));
      Tensor weights = MinTriangleAreaSquared.INSTANCE.origin(polygon);
      {
        Tensor origin = weights.dot(polygon).extract(0, 2);
        LeversRender leversRender = //
            LeversRender.of(geodesicDisplay, sequence, origin, geometricLayer, graphics);
        leversRender.renderWeights(weights);
        leversRender.renderOrigin();
        leversRender.renderLevers(weights);
      }
      {
        Tensor origin = PolygonCentroid.of(sequence);
        LeversRender leversRender = //
            LeversRender.of(geodesicDisplay, sequence, origin, geometricLayer, graphics);
        leversRender.renderOrigin();
      }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new MinTriangleAreaSquaredDemo().setVisible(1000, 800);
  }
}
