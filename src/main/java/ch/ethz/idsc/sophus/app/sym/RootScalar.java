// code by jph
package ch.ethz.idsc.sophus.app.sym;

import java.io.Serializable;
import java.math.MathContext;
import java.util.Objects;

import ch.ethz.idsc.tensor.AbstractScalar;
import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.MachineNumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.api.AbsInterface;
import ch.ethz.idsc.tensor.api.ExactScalarQInterface;
import ch.ethz.idsc.tensor.api.MachineNumberQInterface;
import ch.ethz.idsc.tensor.api.NInterface;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Q[Sqrt[n]] */
public class RootScalar extends AbstractScalar implements //
    AbsInterface, ExactScalarQInterface, MachineNumberQInterface, NInterface, Serializable {
  /** creator with package visibility
   * 
   * @param re neither a {@link ComplexScalar}, or {@link Quantity}
   * @param im neither a {@link ComplexScalar}, or {@link Quantity}
   * @return */
  /* package */ static Scalar of(Scalar re, Scalar im, Scalar ba) {
    return Scalars.isZero(im) || Scalars.isZero(ba) //
        ? re
        : new RootScalar(re, im, ba);
  }

  /***************************************************/
  private final Scalar re;
  private final Scalar im;
  private final Scalar ba;

  /* package */ RootScalar(Scalar re, Scalar im, Scalar ba) {
    this.re = re;
    this.im = im;
    this.ba = ba;
  }

  @Override // from Scalar
  public Scalar negate() {
    return new RootScalar(re.negate(), im.negate(), ba);
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof RealScalar)
      // LONGTERM check for exact precision
      return new RootScalar(re.multiply(scalar), im.multiply(scalar), ba);
    if (scalar instanceof RootScalar) {
      RootScalar rootScalar = (RootScalar) scalar;
      if (ba.equals(rootScalar.ba)) {
        return of( //
            re.multiply(rootScalar.re).add(im.multiply(rootScalar.im).multiply(ba)), //
            re.multiply(rootScalar.im).add(im.multiply(rootScalar.re)), //
            ba);
      }
    }
    throw new UnsupportedOperationException();
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    Scalar den = re.multiply(re).subtract(im.multiply(im).multiply(ba));
    return new RootScalar(re.divide(den), im.negate().divide(den), ba);
  }

  @Override // from Scalar
  public Scalar zero() {
    return re.zero().add(im.zero());
  }

  @Override // from Scalar
  public Scalar one() {
    return re.one();
  }

  @Override // from Scalar
  public Number number() {
    return n().number();
  }

  /***************************************************/
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof RealScalar)
      return new RootScalar(re.add(scalar), im, ba);
    if (scalar instanceof RootScalar) {
      RootScalar rootScalar = (RootScalar) scalar;
      if (ba.equals(rootScalar.ba))
        return of( //
            re.add(rootScalar.re), //
            im.add(rootScalar.im), //
            ba);
    }
    throw new UnsupportedOperationException();
  }

  /***************************************************/
  @Override // from AbsInterface
  public Scalar abs() { // "complex modulus"
    return Abs.FUNCTION.apply(re.add(im.multiply(Sqrt.of(ba))));
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return Abs.FUNCTION.apply(multiply(this));
  }

  @Override // from ExactNumberInterface
  public boolean isExactScalar() {
    return ExactScalarQ.of(re) //
        && ExactScalarQ.of(im) //
        && ExactScalarQ.of(ba);
  }

  @Override // MachineNumberQInterface
  public boolean isMachineNumber() {
    return MachineNumberQ.of(re) //
        && MachineNumberQ.of(im);
  }

  @Override // from NInterface
  public Scalar n() {
    return re.add(im.multiply(Sqrt.of(ba)));
    // return RealScalar.of(N.DOUBLE.apply(re), N.DOUBLE.apply(im));
  }

  @Override // from NInterface
  public Scalar n(MathContext mathContext) {
    N n = N.in(mathContext.getPrecision());
    return n.apply(re).add(n.apply(im).multiply(Sqrt.of(n.apply(ba))));
  }

  /***************************************************/
  @Override // from AbstractScalar
  public int hashCode() {
    return Objects.hash(re, im, ba);
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    if (object instanceof RootScalar) {
      RootScalar rootScalar = (RootScalar) object;
      return re.equals(rootScalar.re) //
          && im.equals(rootScalar.im) //
          && ba.equals(rootScalar.ba);
    }
    return false;
  }

  @Override // from AbstractScalar
  public String toString() {
    return String.format("(%s, %s[%s])", re, im, ba);
  }
}
