// code by bapaden and jph
package ch.ethz.idsc.owl.math.state;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

/** StateTime is immutable, contents of instance do not change after construction */
public final class StateTime implements Serializable {
  private final Tensor x;
  private final Scalar time;

  /** @param x the state
   * @param time the time of the state
   * @throws Exception if either of the input parameters is null */
  public StateTime(Tensor x, Scalar time) {
    GlobalAssert.that(Objects.nonNull(time));
    this.x = x.unmodifiable();
    this.time = time;
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
    return x.copy().append(time);
  }

  public String toInfoString() {
    return String.format("t=%s  x=%s", time(), state().toString());
  }

  public String toCompactString() {
    return String.format("t=%s  x=%s", time(), state().map(Round._4).toString());
  }

  @Override // from Object
  public int hashCode() {
    return Objects.hash(x, time);
  }

  @Override // from Object
  public boolean equals(Object object) {
    if (object instanceof StateTime) {
      StateTime stateTime = (StateTime) object;
      return state().equals(stateTime.state()) && time().equals(stateTime.time());
    }
    return false;
  }
}
