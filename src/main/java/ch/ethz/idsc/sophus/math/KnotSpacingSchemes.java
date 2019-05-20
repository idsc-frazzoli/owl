// code by ob
package ch.ethz.idsc.sophus.math;

import java.util.function.Function;

import ch.ethz.idsc.tensor.TensorMetric;

public enum KnotSpacingSchemes {
  UNIFORM(CentripetalKnotSpacingHelper::uniform), //
  CHORDAL(CentripetalKnotSpacingHelper::chordal)
  // TODO OB/JPH: is this solvable in this structure?
  // ,CENTRIPETAL(CentripetalKnotSpacingHelper::cenrtipetal)
  ;
  public final Function<TensorMetric, CentripetalKnotSpacing> function;

  private KnotSpacingSchemes(Function<TensorMetric, CentripetalKnotSpacing> function) {
    this.function = function;
  }
}