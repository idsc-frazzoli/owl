// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.math.group.Se2Geodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Round;

public class CurveSubdivisionInterpolationApproximation implements Serializable {
  private final TensorUnaryOperator tensorUnaryOperator;

  public CurveSubdivisionInterpolationApproximation(TensorUnaryOperator tensorUnaryOperator) {
    this.tensorUnaryOperator = tensorUnaryOperator;
  }

  public Tensor fixed(Tensor tar, int limit) {
    Tensor ctr = tar.copy();
    for (int count = 0; count < limit; ++count) {
      Tensor ref = tensorUnaryOperator.apply(ctr);
      ref = tensorUnaryOperator.apply(ref);
      Tensor apx = Tensor.of(IntStream.range(0, ref.length()).filter(i -> i % 4 == 0).mapToObj(ref::get));
      Tensor err = tar.subtract(apx);
      ctr = ctr.add(err);
      if (limit - 1 == count) {
        Scalar dev = err.stream().map(Norm._2::ofVector).reduce(Scalar::add).get();
        System.out.println(dev);
      }
    }
    return ctr;
  }

  public static void rn() {
    CurveSubdivision curveSubdivision = new BSpline3CurveSubdivision(Se2Geodesic.INSTANCE);
    CurveSubdivisionInterpolationApproximation interpolatoryApproximation = new CurveSubdivisionInterpolationApproximation(curveSubdivision::string);
    Tensor tar = N.DOUBLE.of(Tensors.fromString("{{0,0,0},{1,1,1},{2,0,0},{3,2,0}}")).unmodifiable();
    final int limit = 20;
    Tensor ctr = interpolatoryApproximation.fixed(tar, limit);
    System.out.println(Pretty.of(ctr.map(Round._3)));
  }

  public static void main(String[] args) {
    rn();
  }
}
