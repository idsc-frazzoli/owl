// code by jph
package ch.ethz.idsc.owl.plot;

import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class ClipExpand {
  private static final ScalarUnaryOperator LOG10 = Log.base(RealScalar.of(5));
  // ---
  final Clip clip;
  final Tensor linspace;

  public ClipExpand(Clip _clip, int resolution) {
    Scalar _width = _clip.width();
    Scalar width = Scalars.isZero(_width) ? RealScalar.ONE : _width;
    // ---
    Scalar unit = width.divide(RealScalar.of(resolution));
    Scalar log = LOG10.apply(unit);
    Scalar inc = Ceiling.FUNCTION.apply(log);
    Scalar increment = Power.of(5, inc);
    clip = Clip.function( //
        Floor.toMultipleOf(increment).apply(_clip.min()), //
        Ceiling.toMultipleOf(increment).apply(_clip.max()));
    Scalar steps = clip.width().divide(increment);
    boolean valid = IntegerQ.of(steps);
    valid &= Scalars.lessEquals(clip.min(), _clip.min());
    valid &= Scalars.lessEquals(_clip.max(), clip.max());
    if (!valid)
      throw TensorRuntimeException.of(_clip.min(), _clip.max());
    linspace = Subdivide.of(clip.min(), clip.max(), steps.number().intValue());
  }
}
