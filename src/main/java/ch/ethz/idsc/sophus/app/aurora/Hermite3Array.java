// code by jph
package ch.ethz.idsc.sophus.app.aurora;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.ref.d1h.Hermite3Subdivisions;
import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Log;

/* package */ class Hermite3Array extends HermiteArray {
  public Hermite3Array(String name, Scalar period, int levels) throws IOException {
    super(name, period, levels);
  }

  private Scalar h3(Scalar theta, Scalar omega) {
    return process(Hermite3Subdivisions.of(HS_EXPONENTIAL, HS_TRANSPORT, theta, omega));
  }

  @Override // from HermiteArray
  Tensor compute(int rows, int cols) {
    Tensor theta = Subdivide.of(RationalScalar.of(-1, 8), RationalScalar.of(1, 8), rows - 1);
    Tensor omega = Subdivide.of(RationalScalar.of(-1, 8), RationalScalar.of(1, 8), cols - 1);
    return Parallelize.matrix((i, j) -> h3(theta.Get(i), omega.Get(j)), rows, cols);
  }

  public static void main(String[] args) throws IOException {
    int levels = 4;
    HermiteArray hermiteArray = //
        new Hermite3Array("20190701T163225_01", Quantity.of(RationalScalar.of(1, 1), "s"), levels);
    File folder = HomeDirectory.Pictures(hermiteArray.getClass().getSimpleName(), String.format("cx_%1d", levels));
    folder.mkdirs();
    Tensor matrix = hermiteArray.getMatrix();
    export(new File(folder, "id"), matrix);
    export(new File(folder, "ln"), matrix.map(RealScalar.ONE::add).map(Log.FUNCTION));
  }
}
