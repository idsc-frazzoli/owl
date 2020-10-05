// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.hs.PrimAlgorithm;
import ch.ethz.idsc.sophus.hs.PrimAlgorithm.Edge;
import ch.ethz.idsc.sophus.hs.sn.SnRandomSample;
import ch.ethz.idsc.sophus.math.DistanceMatrix;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ class S2MinimumSpanningTreeDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  // ---
  private final PathRender pathRenderHull = new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f);

  public S2MinimumSpanningTreeDemo() {
    super(true, GeodesicDisplays.S2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterface(pathRenderHull);
    // ---
    Tensor points = RandomSample.of(SnRandomSample.of(2), 50);
    points.set(Abs.FUNCTION, Tensor.ALL, 2);
    setControlPointsSe2(points);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    Tensor domain = Subdivide.of(0.0, 1.0, 10);
    if (0 < control.length()) {
      Tensor matrix = DistanceMatrix.of(control, geodesicDisplay.parametricDistance());
      List<Edge> list = PrimAlgorithm.of(matrix);
      graphics.setColor(Color.BLACK);
      for (Edge edge : list) {
        Tensor p = control.get(edge.i);
        Tensor q = control.get(edge.j);
        ScalarTensorFunction curve = geodesicInterface.curve(p, q);
        Path2D line = geometricLayer.toPath2D(domain.map(curve));
        graphics.draw(line);
      }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new S2MinimumSpanningTreeDemo().setVisible(1000, 600);
  }
}
