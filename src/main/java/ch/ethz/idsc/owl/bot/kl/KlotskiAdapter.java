// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class KlotskiAdapter implements KlotskiProblem, Serializable {
  public static KlotskiProblem create(Tensor board, String name, Tensor size, Tensor goal, Tensor frame) {
    int sx = size.Get(0).number().intValue();
    int sy = size.Get(1).number().intValue();
    int[][] array = new int[sx][sy];
    for (int px = 0; px < sx; ++px) {
      array[px][0] = 1;
      array[px][sy - 1] = 1;
    }
    for (int py = 0; py < sy; ++py) {
      array[0][py] = 1;
      array[sx - 1][py] = 1;
    }
    for (Tensor stone : board) {
      int index = stone.Get(0).number().intValue();
      int px = stone.Get(1).number().intValue();
      int py = stone.Get(2).number().intValue();
      switch (index) {
      case 0:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        ++array[px + 1][py + 1];
        break;
      case 1:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        break;
      case 2:
        ++array[px + 0][py + 0];
        ++array[px + 0][py + 1];
        break;
      case 3:
        ++array[px + 0][py + 0];
        break;
      case 4:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 2][py + 0];
        break;
      case 5:
        ++array[px + 0][py + 0];
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        break;
      case 6:
        ++array[px + 1][py + 0];
        ++array[px + 0][py + 1];
        ++array[px + 1][py + 1];
        break;
      default:
        throw new RuntimeException("unknown: " + index);
      }
    }
    // System.out.println(Pretty.of(Tensors.matrixInt(array)));
    return new KlotskiAdapter( //
        Tensors.of(board, Tensors.matrixInt(array)), //
        name, size, goal, frame);
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
  public Tensor getStones() {
    return board.get(0);
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
