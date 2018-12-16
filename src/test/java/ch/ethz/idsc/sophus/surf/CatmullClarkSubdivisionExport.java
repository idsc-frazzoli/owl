// code by jph
package ch.ethz.idsc.sophus.surf;

import java.io.IOException;
import java.util.Random;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Sqrt;

enum CatmullClarkSubdivisionExport {
  ;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  private static Tensor univariate() {
    Distribution distribution = NormalDistribution.standard();
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(RnGeodesic.INSTANCE);
    Tensor tensor = RandomVariate.of(distribution, 4, 5);
    return Nest.of(catmullClarkSubdivision::refine, tensor, 3);
  }

  private static Tensor se2() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(Se2Geodesic.INSTANCE);
    Random random = new Random();
    Tensor tensor = Tensors.matrix((i, j) -> Tensors.vector( //
        i + random.nextGaussian() * .2, //
        j + random.nextGaussian() * .2, //
        0 + random.nextGaussian() * .4 //
    ), 5, 6);
    return Nest.of(catmullClarkSubdivision::refine, tensor, 4);
  }

  private static Tensor r3s2() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(R3S2Geodesic.INSTANCE);
    Random random = new Random();
    Tensor tensor = Tensors.matrix((i, j) -> Tensors.of( //
        Tensors.vector( //
            i + random.nextGaussian() * .4, //
            j + random.nextGaussian() * .4, //
            0 + random.nextGaussian() * .4 //
        ), //
        NORMALIZE.apply(Tensors.vector( //
            random.nextDouble() * 0.5, //
            random.nextDouble() * 0.5, //
            random.nextDouble() + .5//
        )) //
    ), 4, 5);
    return Nest.of(catmullClarkSubdivision::refine, tensor, 3);
  }

  private static Tensor r3s2_sp(Number x, Number y) {
    return r3s2_sp(RealScalar.of(x), RealScalar.of(y));
  }

  private static Tensor r3s2_sp(Scalar x, Scalar y) {
    Scalar z = Sqrt.of(RealScalar.ONE.subtract(x.multiply(x)).subtract(y.multiply(y)));
    if (z instanceof RealScalar)
      return Tensors.of(x, y, z);
    throw TensorRuntimeException.of(z);
  }

  private static Tensor r3s2_sphere() {
    CatmullClarkSubdivision catmullClarkSubdivision = new CatmullClarkSubdivision(R3S2Geodesic.INSTANCE);
    Tensor tensor = Tensors.matrix((i, j) -> Tensors.of( //
        r3s2_sp( //
            (i - 1) * .7, //
            (j - 1) * .7), //
        r3s2_sp( //
            (i - 1) * .7, //
            (j - 1) * .7)) //
        , 3, 3);
    return Nest.of(catmullClarkSubdivision::refine, tensor, 3);
  }

  public static void main(String[] args) throws IOException {
    Put.of(UserHome.file("grid.mathematica"), univariate());
    Put.of(UserHome.file("se2.mathematica"), se2());
    Put.of(UserHome.file("r3s2.mathematica"), r3s2());
    Put.of(UserHome.file("sphere.mathematica"), r3s2_sphere());
    // Tensor vector = r3s2_sp(RealScalar.of(.2), RealScalar.of(.3));
    // System.out.println(vector);
  }
}
