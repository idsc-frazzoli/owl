// code by jl
package ch.ethz.idsc.owl.bot.se2.twd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

// TODO JONAS document how the controls look like
public class TwdGlcFlows extends TwdFlows {
  public TwdGlcFlows(Scalar maxSpeed, Scalar halfWidth) {
    super(maxSpeed, halfWidth);
  }

  @Override
  public Collection<Flow> getFlows(int num) {
    int numSqr = num;
    Scalar wheelspeed_max = RealScalar.ONE;
    List<Flow> list = new ArrayList<>();
    Tensor wlList = Subdivide.of(wheelspeed_max.negate(), wheelspeed_max, numSqr);
    Scalar stepSize = wlList.Get(1).subtract(wlList.Get(0));
    for (Tensor _wl : wlList) {
      // |wl|+|wr|<=1
      Scalar wl = _wl.Get();
      Scalar wr = (wheelspeed_max.subtract(wl)).negate();
      while (Scalars.lessEquals(wr, wheelspeed_max.subtract(wl))) {
        list.add(singleton(wl, wr));
        wr = wr.add(stepSize);
      }
    }
    return list;
  }
}
