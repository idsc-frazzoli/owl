// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class Se2DeformationDemo extends AbstractDeformationDemo {
  private static final Tensor ORIGIN = Arrowhead.of(RealScalar.of(0.2));

  Se2DeformationDemo() {
    super(GeodesicDisplays.SE2C_SE2, LogWeightings.list());
    // ---
    timerFrame.configCoordinateOffset(300, 500);
    shuffleSnap();
  }

  @Override
  synchronized Tensor shufflePointsSe2(int n) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Distribution distributionp = UniformDistribution.of(-1, 7);
    Distribution distributiona = UniformDistribution.of(-1, 1);
    return Tensors.vector(i -> geodesicDisplay.project( //
        RandomVariate.of(distributionp, 2).append(RandomVariate.of(distributiona))), n);
  }

  @Override
  MovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    Tensor dx = Subdivide.of(0, 6, res - 1);
    Tensor dy = Subdivide.of(0, 6, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> Tensors.of(dx.get(cx), dy.get(cy), RealScalar.ZERO), dx.length(), dy.length());
    VectorLogManifold vectorLogManifold = geodesicDisplay().vectorLogManifold();
    return new MovingDomain2D(movingOrigin, weightingOperator(vectorLogManifold, movingOrigin), domain);
  }

  @Override
  Tensor shapeOrigin() {
    return ORIGIN;
  }

  @Override
  BiinvariantMean biinvariantMean() {
    return geodesicDisplay().biinvariantMean();
  }

  public static void main(String[] args) {
    new Se2DeformationDemo().setVisible(1000, 800);
  }
}
