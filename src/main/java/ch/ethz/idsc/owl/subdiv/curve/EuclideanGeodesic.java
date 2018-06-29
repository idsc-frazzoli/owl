// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum EuclideanGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return p.multiply(RealScalar.ONE.subtract(scalar)).add(q.multiply(scalar));
  }

  public static void main(String[] args) {
    Tensor tensor = INSTANCE.split(Tensors.vector(10, 1), Tensors.vector(11, 0), RealScalar.of(-1));
    System.out.println(tensor);
  }
}
