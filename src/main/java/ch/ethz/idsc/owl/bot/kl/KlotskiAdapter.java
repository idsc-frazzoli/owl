// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.Serializable;

import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class KlotskiAdapter implements KlotskiProblem, Serializable {
  /** @param board
   * @param name
   * @param stateTimeRaster
   * @param size
   * @param goal
   * @param frame
   * @param border
   * @return */
  public static KlotskiProblem create(Tensor board, String name, StateTimeRaster stateTimeRaster, Tensor size, Tensor goal, Tensor frame, Tensor border) {
    return new KlotskiAdapter(board, name, stateTimeRaster, size, goal, frame, border);
  }

  /***************************************************/
  private final Tensor board;
  private final String name;
  private final StateTimeRaster stateTimeRaster;
  private final Tensor size;
  private final Tensor goal;
  private final Tensor frame;
  private final Tensor border;

  public KlotskiAdapter(Tensor board, String name, StateTimeRaster stateTimeRaster, Tensor size, Tensor goal, Tensor frame, Tensor border) {
    this.board = board;
    this.name = name;
    this.stateTimeRaster = stateTimeRaster;
    this.size = size;
    this.goal = goal;
    this.frame = frame;
    this.border = border;
  }

  @Override // from KlotskiProblem
  public Tensor startState() {
    return board;
  }

  @Override // from KlotskiProblem
  public StateTimeRaster stateTimeRaster() {
    return stateTimeRaster;
  }

  @Override // from KlotskiProblem
  public Tensor size() {
    return size;
  }

  @Override // from KlotskiProblem
  public Tensor getGoal() {
    return goal;
  }

  @Override // from KlotskiProblem
  public Tensor frame() {
    return frame;
  }

  @Override // from KlotskiProblem
  public Tensor getBorder() {
    return border;
  }

  @Override // from KlotskiProblem
  public String name() {
    return name;
  }
}
