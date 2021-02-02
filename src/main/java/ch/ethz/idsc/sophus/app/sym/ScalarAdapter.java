// code by jph
package ch.ethz.idsc.sophus.app.sym;

import ch.ethz.idsc.tensor.AbstractScalar;
import ch.ethz.idsc.tensor.Scalar;

public class ScalarAdapter extends AbstractScalar {
  @Override
  public Scalar multiply(Scalar scalar) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar negate() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar reciprocal() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar zero() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Scalar one() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Number number() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object object) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException();
  }
}
