// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;

public class GeodesicMeanFilter implements TensorUnaryOperator {
  private final GeodesicInterface geodesicInterface;
  private final int radius;
  private Tensor splits = Tensors.empty();

  public GeodesicMeanFilter(GeodesicInterface geodesicInterface, int radius) {
    this.geodesicInterface = geodesicInterface;
    this.radius = radius;
    int width = 2 * radius + 1;
    Scalar weight = RationalScalar.of(1, width);
    Tensor w = Tensors.of(weight);
    for (int c = 0; c < radius - 1; ++c)
      w.append(weight.multiply(RealScalar.of(2)));
    // System.out.println(w);
    Scalar p;
    Scalar factor = RealScalar.ONE;
    for (int c = 0; c < radius; ++c) {
      p = w.Get(c).divide(factor);
      splits.append(p);
      // System.out.println(p);
      factor = factor.multiply(RealScalar.ONE.subtract(p));
    }
    splits = Reverse.of(splits);
    // System.out.println("splits=" + splits);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    // tensor = tensor.extract(0, radius);
    return null;
  }

  public Tensor single(Tensor tensor) {
    Tensor pL = tensor.get(0);
    Tensor pR = tensor.get(2 * radius);
    for (int index = 0; index < radius; ++index) {
      Scalar w = splits.Get(index);
      int pos = index + 1;
      pL = geodesicInterface.split(tensor.get(pos), pL, w);
      pR = geodesicInterface.split(tensor.get(2 * radius - pos), pR, w);
    }
    return geodesicInterface.split(pL, pR, RationalScalar.HALF);
  }

  public static void main(String[] args) {
    GeodesicMeanFilter geodesicMeanFilter = new GeodesicMeanFilter(RnGeodesic.INSTANCE, 3);
    Tensor input = Tensors.vector(1, 2, 3, 4, 5, 6, 7);
    System.out.println("result=" + geodesicMeanFilter.single(input));
    System.out.println("expect=" + Mean.of(input));
  }
}
