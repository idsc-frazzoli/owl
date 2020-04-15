// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogMetricWeightings;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ class H2DeformationDemo extends AbstractDeformationDemo {
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.05));
  // ---
  private final SpinnerLabel<HnMeans> spinnerMeans = SpinnerLabel.of(HnMeans.values());

  H2DeformationDemo() {
    super(GeodesicDisplays.H2_ONLY, LogMetricWeightings.list());
    // ---
    spinnerMeans.setValue(HnMeans.EXACT);
    spinnerMeans.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "hn means");
    // ---
    shuffleSnap();
  }

  @Override // from AbstractDeformationDemo
  synchronized Tensor shufflePointsSe2(int n) {
    return Tensor.of(CirclePoints.of(n).multiply(RealScalar.of(3)).stream().map(row -> row.append(RealScalar.ZERO)));
  }

  @Override // from AbstractDeformationDemo
  MovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    double rad = 1.0;
    Tensor dx = Subdivide.of(-rad, rad, res - 1);
    Tensor dy = Subdivide.of(-rad, rad, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> HnWeierstrassCoordinate.toPoint(Tensors.of(dx.get(cx), dy.get(cy))), dx.length(), dy.length());
    FlattenLogManifold flattenLogManifold = geodesicDisplay().flattenLogManifold();
    TensorMetric tensorMetric = geodesicDisplay().parametricDistance();
    return new MovingDomain2D(movingOrigin, weightingInterface(flattenLogManifold, tensorMetric), domain);
  }

  @Override // from AbstractDeformationDemo
  BiinvariantMean biinvariantMean() {
    return spinnerMeans.getValue().get();
  }

  @Override // from AbstractDeformationDemo
  Tensor shapeOrigin() {
    return TRIANGLE;
  }

  public static void main(String[] args) {
    new H2DeformationDemo().setVisible(1000, 800);
  }
}
