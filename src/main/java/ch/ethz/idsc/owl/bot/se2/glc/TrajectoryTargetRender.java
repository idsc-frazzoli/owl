package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;

import java.awt.Shape;
import java.util.Optional;

/* package */ interface TrajectoryTargetRender {
  Optional<Shape> toTarget(GeometricLayer geometricLayer);
}
