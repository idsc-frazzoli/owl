// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LeversHud;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gbc.AffineCoordinate;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.Genesis;
import ch.ethz.idsc.sophus.ply.d2.Barycenter;
import ch.ethz.idsc.sophus.ply.d2.ThreePointCoordinate;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Append;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.mat.LeastSquares;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;

/* package */ class CirclePointDemo extends ControlPointsDemo {
  public CirclePointDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    Tensor sequence = Tensor.of(CirclePoints.of(7).multiply(RealScalar.of(2)).stream().map(PadRight.zeros(3)));
    setControlPointsSe2(sequence);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderControlPoints(geometricLayer, graphics);
    // ---
    Tensor sequence = getGeodesicControlPoints();
    Tensor levers = Tensor.of(sequence.stream().map(Vector2Norm.NORMALIZE));
    {
      LeversRender leversRender = LeversRender.of( //
          manifoldDisplay(), sequence, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSurfaceP();
    }
    {
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(CirclePoints.of(31), true));
      LeversRender leversRender = LeversRender.of( //
          manifoldDisplay(), levers, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      if (2 < sequence.length()) {
        // ---
        Genesis genesis = ThreePointCoordinate.of(Barycenter.MEAN_VALUE);
        Tensor weights = genesis.origin(levers);
        leversRender.renderWeights(weights);
      }
    }
    geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(5, 0)));
    {
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(CirclePoints.of(31), true));
      LeversRender leversRender = LeversRender.of( //
          manifoldDisplay(), levers, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      leversRender.renderInfluenceX(LeversHud.COLOR_DATA_GRADIENT);
      if (2 < sequence.length()) {
        Genesis genesis = AffineCoordinate.INSTANCE;
        Tensor weights = genesis.origin(levers);
        leversRender.renderWeights(weights);
        Tensor lhs = Tensor.of(levers.stream().map(lever -> Append.of(lever, RealScalar.ONE)));
        Tensor rhs = weights;
        Tensor sol = LeastSquares.of(lhs, rhs);
        // System.out.println(sol.map(Chop._12));
        Tensor dir = sol.extract(0, 2);
        graphics.setColor(Color.RED);
        graphics.draw(geometricLayer.toLine2D(dir));
      }
    }
    geometricLayer.popMatrix();
  }

  public static void main(String[] args) {
    new CirclePointDemo().setVisible(1300, 900);
  }
}
