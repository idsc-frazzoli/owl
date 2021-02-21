// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.java.util.DisjointSets;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LogWeightingDemo;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.MinimumSpanningTree;
import ch.ethz.idsc.sophus.math.MinimumSpanningTree.Edge;
import ch.ethz.idsc.sophus.math.MinimumSpanningTree.EdgeComparator;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.lie.Symmetrize;
import ch.ethz.idsc.tensor.mat.SymmetricMatrixQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class MinimumSpanningTreeDemo extends LogWeightingDemo {
  final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();

  public MinimumSpanningTreeDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_S2_H2_R2, Arrays.asList(LogWeightings.DISTANCES));
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(2);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    Distribution distribution = UniformDistribution.of(-4, 4);
    setControlPointsSe2(RandomVariate.of(distribution, 20, 3));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    RenderQuality.setQuality(graphics);
    Tensor sequence = getGeodesicControlPoints();
    Tensor domain = Subdivide.of(0.0, 1.0, 10);
    final int splits = spinnerRefine.getValue();
    DisjointSets disjointSets = DisjointSets.allocate(sequence.length());
    if (0 < sequence.length()) {
      Tensor matrix = distanceMatrix(sequence);
      List<Edge> list = MinimumSpanningTree.of(matrix);
      Collections.sort(list, new EdgeComparator(matrix));
      int count = Math.max(0, list.size() - splits);
      {
        for (Edge edge : list.subList(0, count))
          disjointSets.union(edge.i, edge.j);
      }
      graphics.setColor(Color.BLACK);
      for (Edge edge : list.subList(0, count)) {
        Tensor p = sequence.get(edge.i);
        Tensor q = sequence.get(edge.j);
        ScalarTensorFunction curve = geodesicInterface.curve(p, q);
        Path2D line = geometricLayer.toPath2D(domain.map(curve));
        graphics.draw(line);
      }
    }
    Map<Integer, Integer> map = disjointSets.createMap(new AtomicInteger()::getAndIncrement);
    for (int index = 0; index < sequence.length(); ++index) {
      int unique = map.get(disjointSets.key(index));
      Color color = ColorDataLists._097.cyclic().getColor(unique);
      PointsRender pointsRender = new PointsRender(color, color);
      pointsRender.show(manifoldDisplay()::matrixLift, getControlPointShape(), Tensors.of(sequence.get(index))).render(geometricLayer, graphics);
    }
  }

  public Tensor distanceMatrix(Tensor sequence) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    TensorUnaryOperator tuo = biinvariant().distances(geodesicDisplay.hsManifold(), sequence);
    Tensor matrix = Tensor.of(sequence.stream().map(tuo));
    return SymmetricMatrixQ.of(matrix) //
        ? matrix
        : Symmetrize.of(matrix);
  }

  public static void main(String[] args) {
    new MinimumSpanningTreeDemo().setVisible(1000, 600);
  }
}
