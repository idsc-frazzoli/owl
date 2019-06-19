// code by jph
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class HeBarycenter implements TensorUnaryOperator {
  private final Tensor lhs;

  public HeBarycenter(Tensor sequence) {
    Tensor tX = Transpose.of(sequence.get(Tensor.ALL, 0));
    Tensor tY = Transpose.of(sequence.get(Tensor.ALL, 1));
    lhs = Join.of(tX, tY);
    Tensor tensor = HeBiinvariantMean.xydot(sequence);
    Tensor z = sequence.get(Tensor.ALL, 2);
    lhs.append(z.subtract(tensor.multiply(RationalScalar.HALF)));
    lhs.append(Tensors.vector(l -> RealScalar.ONE, sequence.length()));
  }

  @Override
  public Tensor apply(Tensor mean) {
    Tensor mXY = mean.get(0).dot(mean.get(1));
    Tensor rhs = Join.of(mean.get(0), mean.get(1));
    rhs.append(mean.Get(2).subtract(mXY.multiply(RationalScalar.HALF)));
    rhs.append(RealScalar.ONE);
    return LinearSolve.of(lhs, rhs);
  }
}
