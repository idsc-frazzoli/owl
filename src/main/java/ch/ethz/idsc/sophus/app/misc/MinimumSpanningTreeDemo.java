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
import ch.ethz.idsc.sophus.hs.Biinvariants;
import ch.ethz.idsc.sophus.hs.PrimAlgorithm;
import ch.ethz.idsc.sophus.hs.PrimAlgorithm.Edge;
import ch.ethz.idsc.sophus.math.DistanceMatrix;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class MinimumSpanningTreeDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  // ---
  private final PathRender pathRenderHull = new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f);

  public MinimumSpanningTreeDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_S2_H2_R2);
    // ---
    timerFrame.geometricComponent.addRenderInterface(pathRenderHull);
    // ---
    Distribution distribution = UniformDistribution.of(-4, 4);
    setControlPointsSe2(RandomVariate.of(distribution, 20, 3));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    RenderQuality.setQuality(graphics);
    Tensor sequence = getGeodesicControlPoints();
    Tensor domain = Subdivide.of(0.0, 1.0, 10);
    if (0 < sequence.length()) {
      Tensor matrix = distanceMatrix(sequence);
      List<Edge> list = PrimAlgorithm.of(matrix);
      graphics.setColor(Color.BLACK);
      for (Edge edge : list) {
        Tensor p = sequence.get(edge.i);
        Tensor q = sequence.get(edge.j);
        ScalarTensorFunction curve = geodesicInterface.curve(p, q);
        Path2D line = geometricLayer.toPath2D(domain.map(curve));
        graphics.draw(line);
      }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public Tensor distanceMatrix(Tensor sequence) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    if (!geodesicDisplay.isMetricBiinvariant()) {
      TensorUnaryOperator tuo = Biinvariants.HARBOR.distances(geodesicDisplay.vectorLogManifold(), sequence);
      return Tensor.of(sequence.stream().map(tuo));
    }
    return DistanceMatrix.of(sequence, geodesicDisplay.parametricDistance());
  }

  public static void main(String[] args) {
    new MinimumSpanningTreeDemo().setVisible(1000, 600);
  }
}
