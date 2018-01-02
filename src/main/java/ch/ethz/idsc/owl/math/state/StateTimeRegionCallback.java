// code by jph
package ch.ethz.idsc.owl.math.state;

public interface StateTimeRegionCallback extends StateTimeCollector {
  /** function is called to indicate member ship of given stateTime in region
   * 
   * @param stateTime */
  void notify_isMember(StateTime stateTime);
}
