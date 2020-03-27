// code by jph
package ch.ethz.idsc.sophus.app.aurora;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.crv.hermite.Hermite1Subdivisions;
import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class Hermite1Array extends HermiteArray {
  public Hermite1Array(String name, Scalar period, int levels) throws IOException {
    super(name, period, levels);
  }

  private Scalar h1(Scalar lambda, Scalar mu) {
    return process(Hermite1Subdivisions.of(LIE_GROUP, LIE_EXPONENTIAL, lambda, mu));
  }

  @Override // from HermiteArray
  Tensor compute(int rows, int cols) {
    Tensor lambda = N.DOUBLE.of(Subdivide.of(RationalScalar.of(-3, 4), RationalScalar.of(-1, 6), rows - 1));
    Tensor mu = N.DOUBLE.of(Subdivide.of(RationalScalar.of(-2, 1), RationalScalar.of(+5, 2), cols - 1));
    return Parallelize.matrix((i, j) -> h1(lambda.Get(i), mu.Get(j)), rows, cols);
    // return Parallelize.matrix((i, j) -> lambda.Get(i), rows, cols);
  }

  public static void main(String[] args) throws IOException {
    String name = "20190701T163225_01";
    name = "20190701T170957_03";
    // name = "20190701T174152_03";
    int levels = 4;
    HermiteArray hermiteArray = //
        new Hermite1Array(name, Quantity.of(RationalScalar.of(1, 1), "s"), levels);
    File folder = HomeDirectory.Pictures(hermiteArray.getClass().getSimpleName(), String.format("xtb3_%1d", levels));
    folder.mkdirs();
    Tensor matrix = hermiteArray.getMatrix();
    HermiteArray.export(new File(folder, "id"), matrix);
    HermiteArray.export(new File(folder, "ln"), matrix.map(RealScalar.ONE::add).map(Log.FUNCTION));
  }
}
