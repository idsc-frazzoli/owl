// code by jph
package ch.ethz.idsc.sophus.curve;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;

public class GeodesicBSplineInterpolation implements TensorUnaryOperator {
  private static final Scalar TWO = RealScalar.of(2);
  static final Chop CHOP_DEFAULT = Chop._12;
  static final int MAXITER = 100;
  // ---
  private final GeodesicInterface geodesicInterface;
  private final int degree;

  /** @param lieGroup
   * @param geodesicInterface corresponding to lie group
   * @param degree of underlying b-spline */
  public GeodesicBSplineInterpolation(GeodesicInterface geodesicInterface, int degree) {
    this.geodesicInterface = geodesicInterface;
    this.degree = degree;
  }

  public class Iteration {
    private final Tensor target;
    private final Tensor control;
    private final int steps;

    private Iteration(Tensor target, Tensor control, int steps) {
      this.target = target;
      this.control = control;
      this.steps = steps;
    }

    public Iteration step() {
      Tensor refine = Range.of(0, target.length()).map(geodesicBSplineFunction(control));
      return new Iteration(target, Tensor.of(IntStream.range(0, control.length()) //
          .mapToObj(index -> move(control.get(index), refine.get(index), target.get(index)))), steps + 1);
    }

    public Tensor control() {
      return control;
    }

    public int steps() {
      return steps;
    }
  }

  public final Iteration start(Tensor target) {
    return new Iteration(target, target, 0);
  }

  public final Iteration untilClose(Tensor target, int maxiter, Chop chop) {
    Iteration iteration = start(target);
    Tensor p = iteration.control();
    for (int count = 0; count < maxiter; ++count) {
      iteration = iteration.step();
      Tensor q = iteration.control();
      if (chop.allZero(N.DOUBLE.of(p.subtract(q))))
        break;
      p = q;
    }
    return iteration;
  }

  @Override
  public final Tensor apply(Tensor target) {
    return untilClose(target, MAXITER, CHOP_DEFAULT).control;
  }

  private GeodesicBSplineFunction geodesicBSplineFunction(Tensor control) {
    return GeodesicBSplineFunction.of(geodesicInterface, degree, control);
  }

  /** @param p previous control point position
   * @param e evaluated location of bspline curve
   * @param t target position of bspline curve
   * @return */
  protected Tensor move(Tensor p, Tensor e, Tensor t) {
    // System.out.println("move " + p + " " + e + " " + t);
    Tensor pt = geodesicInterface.split(p, t, RationalScalar.HALF);
    // System.out.println("pt=" + pt);
    // System.out.println("e=" + e);
    // System.out.println("t=" + t);
    Tensor et = geodesicInterface.split(e, t, RationalScalar.HALF);
    // System.out.println("et=" + et);
    // System.out.println("HERE");
    Tensor tf = geodesicInterface.split(et, pt, TWO); // transfer
    // System.out.println("tf=" + tf);
    Tensor ps = geodesicInterface.split(p, tf, TWO); // push
    // System.out.println("ps=" + ps);
    return ps;
  }

  /* package */ Tensor test(Tensor prev, Tensor eval, Tensor goal) {
    return prev.add(goal.subtract(eval)); // not legal, only for testing
  }
}
