// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.crv.Curvature2D;
import ch.ethz.idsc.sophus.crv.subdiv.Hermite1Subdivisions;
import ch.ethz.idsc.sophus.crv.subdiv.Hermite2Subdivisions;
import ch.ethz.idsc.sophus.crv.subdiv.Hermite3Subdivisions;
import ch.ethz.idsc.sophus.crv.subdiv.HermiteSubdivision;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.sophus.lie.so2.So2Lift;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class HermiteDataArray {
  private static final LieGroup LIE_GROUP = Se2CoveringGroup.INSTANCE;
  private static final LieExponential LIE_EXPONENTIAL = Se2CoveringExponential.INSTANCE;
  // private static final BiinvariantMean BIINVARIANT_MEAN = Se2CoveringBiinvariantMean.INSTANCE;
  private static final Function<Scalar, ? extends Tensor> FUNCTION = ColorDataGradients.JET;
  private static final int RES = 192;
  // ---
  private final int levels;
  private final File folder;
  private final Tensor control = Tensors.empty();
  private final Scalar delta;

  /** @param name "20190701T163225_01"
   * @param period 1/2[s]
   * @param levels 4
   * @throws IOException */
  public HermiteDataArray(String name, Scalar period, int levels) throws IOException {
    this.levels = levels;
    folder = HomeDirectory.Documents(name);
    folder.mkdir();
    Tensor data = GokartPoseDataV2.INSTANCE.getPoseVel(name, 1_000);
    data.set(new So2Lift(), Tensor.ALL, 0, 2);
    Scalar rate = GokartPoseDataV2.INSTANCE.getSampleRate();
    delta = QuantityMagnitude.SI().in("s").apply(period);
    int skip = Scalars.intValueExact(period.multiply(rate));
    for (int index = 0; index < data.length(); index += skip)
      control.append(data.get(index));
  }

  private Scalar process(HermiteSubdivision hermiteSubdivision) {
    TensorIteration tensorIteration = hermiteSubdivision.string(delta, control);
    Tensor refined = Do.of(tensorIteration::iterate, levels);
    Tensor vector = Curvature2D.string(Tensor.of(refined.stream().map(point -> point.get(0).extract(0, 2))));
    // Tensor vector = Differences.of(Tensor.of(refined.stream().map(point -> point.get(1, 1))));
    // return Log.FUNCTION.apply(Norm._1.ofVector(vector).add(RealScalar.ONE));
    // Tensor vector = Flatten.of(Differences.of(Tensor.of(refined.stream().map(point -> point.get(1)))));
    return Log.FUNCTION.apply(Norm._1.ofVector(vector).add(RealScalar.ONE));
  }

  /***************************************************/
  private Scalar h1(Scalar lambda, Scalar mu) {
    return process(Hermite1Subdivisions.of(LIE_GROUP, LIE_EXPONENTIAL, lambda, mu));
  }

  private Tensor h1(int n) {
    Tensor lambda = N.DOUBLE.of(Subdivide.of(RationalScalar.of(-1, 1), RealScalar.ZERO, n - 1));
    Tensor mu = N.DOUBLE.of(Subdivide.of(RationalScalar.of(-1, 1), RationalScalar.of(+1, 1), n - 1));
    return Parallelize.matrix((i, j) -> h1(lambda.Get(i), mu.Get(j)), n - 40, n);
  }

  public static void runH1(int levels) throws IOException {
    Scalar period = Quantity.of(RationalScalar.of(1, 1), "s");
    HermiteDataArray hermiteDataArray = new HermiteDataArray("20190701T163225_01", period, levels);
    Tensor matrix = hermiteDataArray.h1(RES);
    Export.of(HomeDirectory.Pictures(String.format("h1_c_%02d.png", levels)), ArrayPlot.of(matrix, FUNCTION));
  }

  /***************************************************/
  private Scalar h2(Scalar lambda, Scalar mu) {
    return process(Hermite2Subdivisions.of(LIE_GROUP, LIE_EXPONENTIAL, lambda, mu));
  }

  private Tensor h2(int n) {
    Tensor lambda = Subdivide.of(RationalScalar.of(-1, 1), RealScalar.ZERO, n - 1);
    Tensor mu = Subdivide.of(RationalScalar.of(-1, 1), RealScalar.ONE, n - 1);
    return Parallelize.matrix((i, j) -> h2(lambda.Get(i), mu.Get(j)), n, n);
  }

  public static void runH2(int levels) throws IOException {
    Scalar period = Quantity.of(RationalScalar.of(1, 1), "s");
    HermiteDataArray hermiteDataArray = new HermiteDataArray("20190701T163225_01", period, levels);
    Tensor matrix = hermiteDataArray.h2(RES);
    Export.of(HomeDirectory.Pictures(String.format("h2_%02d.png", levels)), ArrayPlot.of(matrix, FUNCTION));
  }

  /***************************************************/
  private Scalar h3(Scalar theta, Scalar omega) {
    return process(Hermite3Subdivisions.of(LIE_GROUP, LIE_EXPONENTIAL, theta, omega));
  }

  private Tensor h3(int n) {
    Tensor theta = Subdivide.of(RationalScalar.of(-1, 8), RationalScalar.of(1, 8), n - 1);
    Tensor omega = Subdivide.of(RationalScalar.of(-1, 8), RationalScalar.of(1, 8), n - 1);
    return Parallelize.matrix((i, j) -> h3(theta.Get(i), omega.Get(j)), n, n);
  }

  public static void runH3(int levels) throws IOException {
    Scalar period = Quantity.of(RationalScalar.of(1, 1), "s");
    HermiteDataArray hermiteDataArray = new HermiteDataArray("20190701T163225_01", period, levels);
    Tensor matrix = hermiteDataArray.h3(RES);
    Export.of(HomeDirectory.Pictures(String.format("h3_%02d.png", levels)), ArrayPlot.of(matrix, FUNCTION));
  }

  /***************************************************/
  public static void main(String[] args) throws IOException {
    runH1(4);
  }
}
