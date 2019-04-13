// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.Scalar;

public class PIDGains {
  public final Scalar Kp;
  public final Scalar Ki;
  public final Scalar Kd;

  public PIDGains(Scalar Kp, Scalar Ki, Scalar Kd) {
    this.Kp = Kp;
    this.Ki = Ki;
    this.Kd = Kd;
    // TODO JPH how to GlobalAssert.that(Kd and Kd has correct unit); 
  }
}
