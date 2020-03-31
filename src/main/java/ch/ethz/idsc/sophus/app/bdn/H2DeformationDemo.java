// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.HnBarycentricCoordinates;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class H2DeformationDemo extends AbstractDeformationDemo {
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.05));
  // ---
  private final SpinnerLabel<HnMeans> spinnerMeans = new SpinnerLabel<>();

  H2DeformationDemo() {
    super(GeodesicDisplays.H2_ONLY, HnBarycentricCoordinates.values());
    // ---
    {
      spinnerMeans.setArray(HnMeans.values());
      spinnerMeans.setValue(HnMeans.EXACT);
      spinnerMeans.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "hn means");
    }
    // Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    // timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    // timerFrame.configCoordinateOffset(400, 400);
    // ---
    shuffleSnap();
  }

  @Override
  synchronized Tensor shufflePointsSe2(int n) {
    Distribution distribution = UniformDistribution.of(-1.5, 1.5);
    return Tensor.of(RandomVariate.of(distribution, n, 2).stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(RealScalar.ZERO)));
  }

  @Override
  MovingDomain2D updateMovingDomain2D(Tensor movingOrigin) {
    int res = refinement();
    Tensor dx = Subdivide.of(-1.0, 1.0, res - 1);
    Tensor dy = Subdivide.of(-1.0, 1.0, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> HnWeierstrassCoordinate.toPoint(Tensors.of(dx.get(cx), dy.get(cy))), dx.length(), dy.length());
    return new MovingDomain2D(movingOrigin, weightingInterface(), domain);
  }

  @Override
  BiinvariantMean biinvariantMean() {
    // return HnPhongMean.INSTANCE;
    return geodesicDisplay().biinvariantMean();
    // BiinvariantMean biinvariantMean = spinnerMeans.getValue().get();
    // System.out.println(biinvariantMean.getClass().getSimpleName());
    // return biinvariantMean;
  }

  @Override
  Tensor shapeOrigin() {
    return TRIANGLE;
  }

  public static void main(String[] args) {
    new H2DeformationDemo().setVisible(1000, 800);
  }
}
