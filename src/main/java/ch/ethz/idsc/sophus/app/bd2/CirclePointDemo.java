// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.lev.LeversHud;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gbc.AffineCoordinate;
import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.ThreePointCoordinate;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Append;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.mat.LeastSquares;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ class CirclePointDemo extends ControlPointsDemo {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  // ---
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
    Tensor normalized = Tensor.of(sequence.stream().map(NORMALIZE));
    {
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), sequence, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSurfaceP();
    }
    {
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(CirclePoints.of(31), true));
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), normalized, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      if (2 < sequence.length()) {
        // ---
        Genesis genesis = ThreePointCoordinate.of(Barycenter.MEAN_VALUE);
        Tensor weights = genesis.origin(normalized);
        leversRender.renderWeights(weights);
      }
    }
    geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(5, 0)));
    {
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(CirclePoints.of(31), true));
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), normalized, Array.zeros(2), geometricLayer, graphics);
      leversRender.renderSequence();
      leversRender.renderInfluenceX(LeversHud.COLOR_DATA_GRADIENT);
      if (2 < sequence.length()) {
        Genesis genesis = AffineCoordinate.INSTANCE;
        Tensor weights = genesis.origin(normalized);
        leversRender.renderWeights(weights);
        Tensor lhs = Tensor.of(normalized.stream().map(r -> Append.of(r, RealScalar.ONE)));
        Tensor rhs = weights;
        Tensor sol = LeastSquares.of(lhs, rhs);
        Tensor err = lhs.dot(sol).subtract(rhs);
        if (!Chop._08.allZero(err))
          System.out.println(err.map(Chop._12));
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
