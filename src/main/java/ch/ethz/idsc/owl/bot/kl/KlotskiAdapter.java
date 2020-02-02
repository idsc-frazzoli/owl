// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class KlotskiAdapter implements KlotskiProblem, Serializable {
  public static KlotskiProblem create(Tensor board, String name, Tensor size, Tensor goal, Tensor frame) {
    return new KlotskiAdapter(board, name, size, goal, frame);
  }

  private final Tensor board;
  private final String name;
  private final Tensor size;
  private final Tensor goal;
  private final Tensor frame;

  public KlotskiAdapter(Tensor board, String name, Tensor size, Tensor goal, Tensor frame) {
    this.board = board;
    this.name = name;
    this.size = size;
    this.goal = goal;
    this.frame = frame;
  }

  @Override
  public Tensor getState() {
    return board;
  }

  @Override
  public Tensor size() {
    return size;
  }

  @Override
  public Tensor getGoal() {
    return goal;
  }

  @Override
  public Tensor getFrame() {
    return frame;
  }

  @Override
  public String name() {
    return name;
  }
}
