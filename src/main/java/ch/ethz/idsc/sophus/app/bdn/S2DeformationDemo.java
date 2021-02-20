// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class S2DeformationDemo extends AbstractDeformationDemo {
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.05));
  private static final Scalar ZHEIGHT = RealScalar.of(1.0); // 1.8 for initial pics
  // ---
  private final SpinnerLabel<SnMeans> spinnerSnMeans = SpinnerLabel.of(SnMeans.values());

  S2DeformationDemo() {
    super(GeodesicDisplays.S2_ONLY, LogWeightings.coordinates());
    // ---
    spinnerSnMeans.setValue(SnMeans.FAST);
    spinnerSnMeans.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "sn means");
    // ---
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.geometricComponent.setOffset(400, 400);
    // ---
    shuffleSnap();
    // ---
    setControlPointsSe2(Tensors.fromString( //
        "{{-13/75, 7/20, 0.0}, {19/300, 31/150, 0.0}, {53/150, 101/300, 0.0}, {31/300, -1/100, 0.0}, {-7/25, -7/150, 0.0}, {-1/100, -97/300, 0.0}, {27/100, -29/150, 0.0}, {-9/25, -137/300, 0.0}}"));
    setControlPointsSe2(Tensors.fromString( //
        "{{-13/25, 9/25, 0.0}, {7/100, 7/12, 0.0}, {-9/50, -1/12, 0.0}, {59/100, 1/60, 0.0}, {-2/5, -27/50, 0.0}, {113/300, -133/300, 0.0}}"));
    snap();
  }

  @Override
  synchronized Tensor shufflePointsSe2(int n) {
    Distribution distribution = UniformDistribution.of(-0.5, 0.5);
    return Tensor.of(RandomVariate.of(distribution, n, 2).stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(RealScalar.ZERO)));
  }

  @Override
  MovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    Tensor dx = Subdivide.of(-1, 1, res - 1);
    Tensor dy = Subdivide.of(-1, 1, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> Vector2Norm.NORMALIZE.apply(Tensors.of(dx.get(cx), dy.get(cy), ZHEIGHT)), dx.length(), dy.length());
    TensorUnaryOperator tensorUnaryOperator = operator(movingOrigin);
    return AveragedMovingDomain2D.of(movingOrigin, tensorUnaryOperator, domain);
  }

  @Override
  BiinvariantMean biinvariantMean() {
    return spinnerSnMeans.getValue().get();
  }

  @Override
  Tensor shapeOrigin() {
    return TRIANGLE;
  }

  public static void main(String[] args) {
    new S2DeformationDemo().setVisible(1400, 800);
  }
}
