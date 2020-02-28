// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.RnBarycentricCoordinates;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/** moving least squares */
/* package */ class R2DeformationDemo extends DeformationDemo {
  private static final Tensor ORIGIN = CirclePoints.of(3).multiply(RealScalar.of(0.1));
  private final JToggleButton jToggleMLS = new JToggleButton("MLS");
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();

  R2DeformationDemo() {
    super(GeodesicDisplays.R2_ONLY, RnBarycentricCoordinates.SCATTERED);
    // ---
    {
      jToggleMLS.addActionListener(l -> recomputeMD2D());
      timerFrame.jToolBar.add(jToggleMLS);
    }
    {
      spinnerLength.addSpinnerListener(this::shufflePoints);
      spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
      spinnerLength.setValue(8);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    shufflePoints(spinnerLength.getValue());
    timerFrame.configCoordinateOffset(300, 500);
  }

  private synchronized void shufflePoints(int n) {
    Distribution distribution = UniformDistribution.of(-1, 7);
    setControlPointsSe2(Tensor.of(RandomVariate.of(distribution, n, 2).stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(RealScalar.ZERO))));
    snap();
  }

  @Override
  MovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    Tensor dx = Subdivide.of(0, 6, res - 1);
    Tensor dy = Subdivide.of(0, 6, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> Tensors.of(dx.get(cx), dy.get(cy)), dx.length(), dy.length());
    return jToggleMLS.isSelected() //
        ? new LSMovingDomain2D(movingOrigin, barycentricCoordinate(), domain)
        : new MovingDomain2D(movingOrigin, barycentricCoordinate(), domain);
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
    new R2DeformationDemo().setVisible(1000, 800);
  }
}
