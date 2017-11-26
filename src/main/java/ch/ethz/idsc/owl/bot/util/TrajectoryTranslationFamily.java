// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.map.RigidFamily;
import ch.ethz.idsc.owl.math.map.TranslationFamily;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.sca.Chop;

public class TrajectoryTranslationFamily extends TranslationFamily {
  /** @param stateIntegrator
   * @param initial
   * @param flow
   * @return */
  public static RigidFamily create(StateIntegrator stateIntegrator, StateTime initial, Flow flow) {
    List<StateTime> trajectory = stateIntegrator.trajectory(initial, flow);
    return new TrajectoryTranslationFamily(trajectory, initial);
  }

  // ---
  private final List<Tensor> list = new ArrayList<>();
  private final Scalar ofs;
  private final Scalar delta;
  private final int limit;

  private TrajectoryTranslationFamily(List<StateTime> trajectory, StateTime initial) {
    list.add(initial.state().extract(0, 2));
    trajectory.stream() //
        .map(StateTime::state) //
        .map(tensor -> tensor.extract(0, 2)) //
        .forEach(list::add);
    ofs = initial.time();
    delta = trajectory.get(0).time().subtract(ofs);
    limit = list.size() - 1;
    // ---
    { // consistency check
      Tensor times = Tensor.of(trajectory.stream().map(StateTime::time).map(this::index));
      GlobalAssert.that(Chop._10.close(times, Range.of(1, times.length() + 1)));
    }
  }

  @Override // from AbstractTranslationFamily
  public Tensor function_apply(Scalar scalar) {
    return list.get(Math.min(index(scalar).number().intValue(), limit));
  }

  private Scalar index(Scalar scalar) {
    return scalar.subtract(ofs).divide(delta);
  }
}
