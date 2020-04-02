// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class Se2CoveringDeformationDemo extends AbstractDeformationDemo {
  private static final Tensor ORIGIN = Arrowhead.of(RealScalar.of(0.2));

  Se2CoveringDeformationDemo() {
    super(GeodesicDisplays.SE2C_SE2, Se2CoveringBarycentricCoordinates.values());
    // ---
    timerFrame.configCoordinateOffset(300, 500);
    shuffleSnap();
  }

  @Override
  synchronized Tensor shufflePointsSe2(int n) {
    Distribution distributionp = UniformDistribution.of(-1, 7);
    Distribution distributiona = UniformDistribution.of(-1, 1);
    return Tensors.vector(i -> RandomVariate.of(distributionp, 2).append(RandomVariate.of(distributiona)), n);
  }

  @Override
  MovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    Tensor dx = Subdivide.of(0, 6, res - 1);
    Tensor dy = Subdivide.of(0, 6, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> Tensors.of(dx.get(cx), dy.get(cy), RealScalar.ZERO), dx.length(), dy.length());
    return new MovingDomain2D(movingOrigin, weightingInterface(), domain);
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
    new Se2CoveringDeformationDemo().setVisible(1000, 800);
  }
}
