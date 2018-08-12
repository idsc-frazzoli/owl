// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.red.Norm;

/** agents moves with unit speed, i.e.
 * Euclidean length of line segment equals the time required to traverse */
class RnTransition extends AbstractTransition {
  public RnTransition(Tensor start, Tensor end) {
    super(start, end);
  }

  @Override
  public Scalar length() {
    return Norm._2.between(start(), end());
  }

  @Override
  public List<StateTime> sampled(Scalar t0, Scalar ofs, Scalar dt) {
    if (Scalars.lessThan(dt, ofs))
      throw TensorRuntimeException.of(ofs, dt);
    final Scalar length = length();
    if (Scalars.isZero(length))
      return Collections.emptyList();
    List<StateTime> list = new ArrayList<>();
    while (Scalars.lessThan(ofs, length)) {
      Tensor x = start().multiply(length.subtract(ofs).divide(length)) //
          .add(end().multiply(ofs.divide(length)));
      StateTime stateTime = new StateTime(x, t0.add(ofs));
      list.add(stateTime);
      ofs = ofs.add(dt);
    }
    return list;
  }

  @Override
  public StateTime splitAt(Scalar t1) {
    // not yet implemented, use RnGeodesic
    throw new RuntimeException();
  }
}
