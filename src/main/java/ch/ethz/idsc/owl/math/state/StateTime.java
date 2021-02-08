// code by bapaden and jph
package ch.ethz.idsc.owl.math.state;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Append;

/** StateTime is immutable, contents of instance do not change after construction */
public final class StateTime implements Serializable {
  private final Tensor x;
  private final Scalar time;

  /** @param x the state
   * @param time the time of the state
   * @throws Exception if either of the input parameters is null */
  public StateTime(Tensor x, Scalar time) {
    this.x = x.unmodifiable();
    this.time = Objects.requireNonNull(time);
  }

  /** @return state that was passed to the constructor */
  public Tensor state() {
    return x;
  }

  /** @return time that was passed to the constructor */
  public Scalar time() {
    return time;
  }

  /** @return concatenation of state and time as vector */
  public Tensor joined() {
    return Append.of(x, time);
  }

  public String toInfoString() {
    return String.format("t=%s  x=%s", time(), state().toString());
  }

  @Override // from Object
  public int hashCode() {
    return x.hashCode() + 31 * time.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    if (object instanceof StateTime) {
      StateTime stateTime = (StateTime) object;
      return state().equals(stateTime.state()) //
          && time().equals(stateTime.time());
    }
    return false;
  }
}
