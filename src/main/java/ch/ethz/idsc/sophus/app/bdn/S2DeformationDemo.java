// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ class S2DeformationDemo extends AbstractDeformationDemo {
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.05));
  // ---
  private final SpinnerLabel<SnMeans> spinnerSnMeans = SpinnerLabel.of(SnMeans.values());

  S2DeformationDemo() {
    super(GeodesicDisplays.S2_ONLY, LogWeightings.list());
    // ---
    spinnerSnMeans.setValue(SnMeans.FAST);
    spinnerSnMeans.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "sn means");
    // ---
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(400, 400);
    // ---
    shuffleSnap();
  }

  @Override
  synchronized Tensor shufflePointsSe2(int n) {
    Distribution distribution = UniformDistribution.of(-0.5, 0.5);
    return Tensor.of(RandomVariate.of(distribution, n, 2).stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(RealScalar.ZERO)));
  }

  @Override
  AveragedMovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    Tensor dx = Subdivide.of(-1, 1, res - 1);
    Tensor dy = Subdivide.of(-1, 1, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> NORMALIZE.apply(Tensors.of(dx.get(cx), dy.get(cy), RealScalar.of(1.8))), dx.length(), dy.length());
    VectorLogManifold vectorLogManifold = geodesicDisplay().vectorLogManifold();
    TensorUnaryOperator tensorUnaryOperator = weightingOperator(vectorLogManifold, movingOrigin);
    return new AveragedMovingDomain2D(movingOrigin, tensorUnaryOperator, domain);
  }

  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  @Override
  BiinvariantMean biinvariantMean() {
    return spinnerSnMeans.getValue().get();
  }

  @Override
  Tensor shapeOrigin() {
    return TRIANGLE;
  }

  public static void main(String[] args) {
    new S2DeformationDemo().setVisible(1200, 800);
  }
}
