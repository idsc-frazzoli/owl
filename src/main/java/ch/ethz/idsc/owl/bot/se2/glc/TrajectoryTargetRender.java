// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.Optional;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;

@FunctionalInterface
/* package */ interface TrajectoryTargetRender {
  Optional<Shape> toTarget(GeometricLayer geometricLayer);
}
