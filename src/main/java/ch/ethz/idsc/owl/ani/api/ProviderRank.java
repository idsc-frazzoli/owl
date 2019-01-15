// code by jph
package ch.ethz.idsc.owl.ani.api;

/** rank of a {@link PutProvider} in order of priority
 * 
 * for instance, messages for calibration have higher precedence than messages for testing */
public enum ProviderRank {
  /** not used */
  GODMODE, //
  /** physical imperative hardware protection, for instance
   * 1) when the linmot temperature is above critical limit
   * 2) when steering battery is being charged the steering should be passive
   * otherwise battery may overcharge. */
  HARDWARE, //
  /** emergency is a condition that is not encountered during nominal operation.
   * an emergency state should be acknowledged by the operator.
   * emergency is for instance when the steering battery is low,
   * linmot brake is not calibrated,
   * bumper has contact, flat tire, ... */
  EMERGENCY, //
  /** for instance during calibration of
   * 1) linmot break, or
   * 2) steering */
  CALIBRATION, //
  /** for instance when controlling with joystick */
  MANUAL, //
  /** for instance when testing actuators in gui */
  TESTING, //
  /** for instance when lidar detects approaching obstacle
   * that is too fast to be considered by the path planner
   * safety control may override autonomous logic,
   * or
   * when sensors fail that are obligatory for autonomous mode */
  SAFETY, //
  /** when following a trajectory for instance provided by a path planner */
  AUTONOMOUS, //
  /** if no prior controls have been issued the fallback option is used
   * all systems idle, hand-brake mode */
  FALLBACK, //
  ;
}
