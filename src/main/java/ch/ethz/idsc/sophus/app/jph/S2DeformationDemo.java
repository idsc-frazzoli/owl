// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Dimension;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.SnBarycentricCoordinates;
import ch.ethz.idsc.sophus.app.api.SnMeans;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
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

/* package */ class S2DeformationDemo extends DeformationDemo {
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.05));
  // ---
  private final SpinnerLabel<SnMeans> spinnerSnMeans = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();

  S2DeformationDemo() {
    super(GeodesicDisplays.S2_ONLY, SnBarycentricCoordinates.values());
    // ---
    {
      spinnerSnMeans.setArray(SnMeans.values());
      spinnerSnMeans.setValue(SnMeans.FAST);
      spinnerSnMeans.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "sn means");
    }
    {
      spinnerLength.addSpinnerListener(this::shufflePoints);
      spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
      spinnerLength.setValue(5);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    shufflePoints(spinnerLength.getValue());
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(400, 400);
  }

  private synchronized void shufflePoints(int n) {
    Distribution distribution = UniformDistribution.of(-0.5, 0.5);
    setControlPointsSe2(Tensor.of(RandomVariate.of(distribution, n, 2).stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(RealScalar.ZERO))));
    snap();
  }

  @Override
  void updateMovingDomain2D() {
    int res = refinement();
    Tensor dx = Subdivide.of(-1, 1, res - 1);
    Tensor dy = Subdivide.of(-1, 1, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> NORMALIZE.apply(Tensors.of(dx.get(cx), dy.get(cy), RealScalar.of(1.8))), dx.length(), dy.length());
    movingDomain2D = new MovingDomain2D(movingOrigin, barycentricCoordinate(), domain);
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
    new S2DeformationDemo().setVisible(1000, 800);
  }
}
