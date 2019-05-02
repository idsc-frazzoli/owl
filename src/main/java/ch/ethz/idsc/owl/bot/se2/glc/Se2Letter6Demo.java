// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.ani.api.TrajectoryControl;

public class Se2Letter6Demo extends Se2LetterADemo {
  @Override
  public TrajectoryControl createTrajectoryControl() {
    return new ClothoidFixedControl(CarEntity.LOOKAHEAD, CarEntity.MAX_TURNING_RATE);
  }

  public static void main(String[] args) {
    new Se2Letter6Demo().start().jFrame.setVisible(true);
  }
}
