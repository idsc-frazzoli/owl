// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ enum StaticHelper {
  ;
  public static Scalar distance(Tensor p, Tensor q) {
    return p.subtract(q).flatten(-1) //
        .map(Scalar.class::cast) //
        .map(Abs.FUNCTION) //
        .reduce(Scalar::add).get();
  }

  public static Tensor closest(Tensor tensor, Tensor value, int increment) {
    Tensor distance = Tensor.of(tensor.stream().map(row -> distance(row, value)));
    return tensor.get(Math.min(Math.max(0, ArgMin.of(distance) + increment), tensor.length() - 1));
  }

  public static void main(String[] args) {
    Tensor tensor = closest(Subdivide.of(1, 10, 9), RealScalar.of(3), 1);
    System.out.println(tensor);
  }
}
