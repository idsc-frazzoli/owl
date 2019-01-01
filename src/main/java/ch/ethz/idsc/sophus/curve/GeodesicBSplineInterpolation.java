// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;

public class GeodesicBSplineInterpolation implements Serializable {
  private static final Scalar TWO = RealScalar.of(2);
  private static final Chop CHOP_DEFAULT = Chop._12;
  private static final int MAXITER = 500;
  // ---
  private final GeodesicInterface geodesicInterface;
  private final int degree;
  private final Tensor target;

  /** @param lieGroup
   * @param geodesicInterface corresponding to lie group
   * @param degree of underlying b-spline */
  public GeodesicBSplineInterpolation(GeodesicInterface geodesicInterface, int degree, Tensor target) {
    this.geodesicInterface = geodesicInterface;
    this.degree = degree;
    this.target = target;
  }

  public class Iteration {
    private final Tensor control;
    private final int steps;

    private Iteration(Tensor control, int steps) {
      this.control = control;
      this.steps = steps;
    }

    public Iteration step() {
      Tensor refine = Range.of(0, target.length()).map(geodesicBSplineFunction(control));
      return new Iteration(Tensor.of(IntStream.range(0, control.length()) //
          .mapToObj(index -> move(control.get(index), refine.get(index), target.get(index)))), steps + 1);
    }

    public Tensor control() {
      return control;
    }

    public int steps() {
      return steps;
    }
  }

  public final Iteration init() {
    return new Iteration(target, 0);
  }

  public final Iteration untilClose(Chop chop, int maxiter) {
    Iteration iteration = init();
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

  public final Tensor apply() {
    return untilClose(CHOP_DEFAULT, MAXITER).control;
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
