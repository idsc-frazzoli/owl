// code by jph
package ch.ethz.idsc.sophus.app.aurora;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.ref.d1h.Hermite2Subdivisions;
import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Log;

/* package */ class Hermite2Array extends HermiteArray {
  public Hermite2Array(String name, Scalar period, int levels) throws IOException {
    super(name, period, levels);
  }

  private Scalar h2(Scalar lambda, Scalar mu) {
    return process(Hermite2Subdivisions.of(HS_EXPONENTIAL, HS_TRANSPORT, lambda, mu));
  }

  @Override // from HermiteArray
  Tensor compute(int rows, int cols) {
    Tensor mu = Subdivide.of(RationalScalar.of(-1, 1), RationalScalar.of(+2, 1), rows - 1);
    Tensor lambda = Subdivide.of(RationalScalar.of(-2, 1), RationalScalar.of(+3, 1), cols - 1);
    return Parallelize.matrix((i, j) -> h2(lambda.Get(j), mu.Get(i)), rows, cols);
  }

  public static void main(String[] args) throws IOException {
    int levels = 4;
    HermiteArray hermiteArray = //
        new Hermite2Array("20190701T163225_01", Quantity.of(RationalScalar.of(1, 1), "s"), levels);
    File folder = HomeDirectory.Pictures(hermiteArray.getClass().getSimpleName(), String.format("cs_%1d", levels));
    folder.mkdirs();
    Tensor matrix = hermiteArray.getMatrix();
    export(new File(folder, "id"), matrix);
    export(new File(folder, "ln"), matrix.map(RealScalar.ONE::add).map(Log.FUNCTION));
  }
}
