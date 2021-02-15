// code by jph
package ch.ethz.idsc.sophus.app.aurora;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.r2.Se2Parametric;
import ch.ethz.idsc.sophus.lie.LieTransport;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringManifold;
import ch.ethz.idsc.sophus.lie.so2.So2Lift;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.sophus.ref.d1h.HermiteSubdivision;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;

/* package */ abstract class ProxyHermite {
  static final HsManifold HS_EXPONENTIAL = Se2CoveringManifold.INSTANCE;
  static final HsTransport HS_TRANSPORT = LieTransport.INSTANCE;
  // private static final BiinvariantMean BIINVARIANT_MEAN = Se2CoveringBiinvariantMean.INSTANCE;
  static final Function<Scalar, ? extends Tensor> FUNCTION = ColorDataGradients.JET;
  private static final int ROWS = 135 * 1;
  private static final int COLS = 240 * 1;
  // ---
  private final int levels;
  private final File folder;
  private final Tensor data;
  private final Tensor control;
  private final Scalar delta;
  private final Tensor matrix;

  /** @param name "20190701T163225_01"
   * @param levels 2
   * @throws IOException */
  public ProxyHermite(String name, int levels) throws IOException {
    this.levels = levels;
    folder = HomeDirectory.Documents(name);
    folder.mkdir();
    Scalar rate = GokartPoseDataV2.INSTANCE.getSampleRate();
    int delta2 = 1;
    for (int level = 0; level < levels; ++level) {
      delta2 *= 2;
      rate = rate.multiply(RationalScalar.HALF);
    }
    delta = QuantityMagnitude.SI().in("s").apply(rate.reciprocal());
    System.out.println(delta);
    data = GokartPoseDataV2.INSTANCE.getPoseVel(name, delta2 * 20 + 1);
    data.set(new So2Lift(), Tensor.ALL, 0, 2);
    System.out.println(Dimensions.of(data));
    control = Thinning.of(data, delta2);
    System.out.println(Dimensions.of(control));
    matrix = compute(ROWS, COLS);
  }

  final Scalar process(HermiteSubdivision hermiteSubdivision) {
    TensorIteration tensorIteration = hermiteSubdivision.string(delta, control);
    Tensor refined = Do.of(control, tensorIteration::iterate, levels);
    // TODO not a distance
    if (refined.length() != data.length())
      System.err.println("nonono");
    Scalar total = RealScalar.ZERO;
    for (int index = 0; index < refined.length(); ++index) {
      Tensor p = refined.get(index, 0);
      Tensor q = data.get(index, 0);
      total = total.add(Se2Parametric.INSTANCE.distance(p, q));
    }
    return total;
  }

  final Tensor getMatrix() {
    return matrix;
  }

  abstract Tensor compute(int rows, int cols);

  public static void export(File directory, Tensor matrix) throws IOException {
    directory.mkdir();
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      File file = new File(directory, String.format("%s.png", colorDataGradients));
      Export.of(file, ArrayPlot.of(matrix, colorDataGradients));
    }
  }
}
