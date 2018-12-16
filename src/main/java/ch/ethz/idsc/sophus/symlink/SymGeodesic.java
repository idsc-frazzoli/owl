// code by jph
package ch.ethz.idsc.sophus.symlink;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum SymGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return SymScalar.of(p.Get(), q.Get(), scalar);
  }

  public static void main(String[] args) {
    Scalar s1 = SymScalar.of(1);
    Scalar s2 = SymScalar.of(2);
    SymScalar s3 = (SymScalar) SymScalar.of(s1, s2, RationalScalar.HALF);
    System.out.println(s3);
    System.out.println(s3.evaluate());
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(SymGeodesic.INSTANCE, SmoothingKernel.DIRICHLET);
    Tensor vector = Tensor.of(IntStream.range(0, 5).mapToObj(SymScalar::of));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    System.out.println(tensor);
    SymLink root = SymLink.build((SymScalar) tensor);
    Tensor pose = root.getPosition();
    System.out.println(pose);
    // root.lP;
    System.out.println(root);
    SymScalar res = (SymScalar) tensor;
    System.out.println(res.evaluate());
  }
}
