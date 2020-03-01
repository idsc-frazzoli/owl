// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.jph.ArrayPlotRender;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupOps;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class Se2CoveringAnimationDemo extends LeversDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshotUncentered;
  private Tensor snapshot;

  public Se2CoveringAnimationDemo() {
    super(GeodesicDisplays.SE2C_ONLY);
    {
      timerFrame.jToolBar.add(jToggleAxes);
      jToggleAxes.setSelected(true);
    }
    {
      jToggleAnimate.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (jToggleAnimate.isSelected()) {
            snapshotUncentered = getControlPointsSe2();
            Tensor controlPointsAll = getGeodesicControlPoints();
            if (0 < controlPointsAll.length()) {
              GeodesicDisplay geodesicDisplay = geodesicDisplay();
              LieGroup lieGroup = geodesicDisplay.lieGroup();
              LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
              Tensor origin = controlPointsAll.get(0);
              snapshot = lieGroupOps.allLeft(controlPointsAll, lieGroup.element(origin).inverse().toCoordinate());
            }
          } else
            setControlPointsSe2(snapshotUncentered);
        }
      });
      timerFrame.jToolBar.add(jToggleAnimate);
    }
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}, {-3, 0, 0}}"));
  }

  private static Tensor random(double toc, int index) {
    return Tensors.vector( //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 0) * 3, //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 1) * 3, //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 2));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
    Tensor controlPointsAll = getGeodesicControlPoints();
    if (jToggleAnimate.isSelected())
      if (0 < controlPointsAll.length())
        setControlPointsSe2(lieGroupOps.allConjugate(snapshot, random(10 + timing.seconds() * 0.1, 0)));
    GraphicsUtil.setQualityHigh(graphics);
    new Flush(geodesicDisplay, controlPointsAll).render(geometricLayer, graphics);
  }

  public static class Flush implements RenderInterface {
    private final GeodesicDisplay geodesicDisplay;
    private final Tensor controlPointsAll;

    public Flush(GeodesicDisplay geodesicDisplay, Tensor controlPointsAll) {
      this.geodesicDisplay = geodesicDisplay;
      this.controlPointsAll = controlPointsAll;
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      Tensor shape = geodesicDisplay.shape();
      if (0 < controlPointsAll.length()) {
        Tensor origin = controlPointsAll.get(0);
        if (1 + geodesicDisplay.dimensions() < controlPointsAll.length()) {
          Tensor controlPoints = controlPointsAll.extract(1, controlPointsAll.length());
          BarycentricCoordinate barycentricCoordinate = geodesicDisplay.barycentricCoordinate();
          Tensor weights = barycentricCoordinate.weights(controlPoints, origin);
          {
            for (Tensor q : controlPoints) {
              ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, q);
              graphics.setStroke(STROKE);
              {
                Tensor domain = Subdivide.of(0, 1, 21);
                Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream() //
                    .map(geodesicDisplay::toPoint));
                graphics.setColor(new Color(128, 128, 128, 128));
                graphics.draw(geometricLayer.toPath2D(ms));
              }
            }
            graphics.setStroke(new BasicStroke());
          }
          {
            graphics.setFont(ArrayPlotRender.FONT);
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int fheight = fontMetrics.getAscent();
            int index = 0;
            for (Tensor q : controlPoints) {
              Tensor matrix = geodesicDisplay.matrixLift(q);
              geometricLayer.pushMatrix(matrix);
              Path2D path2d = geometricLayer.toPath2D(shape, true);
              graphics.setColor(Color.BLACK);
              Rectangle rectangle = path2d.getBounds();
              graphics.drawString(" " + weights.Get(index).map(Round._3), //
                  rectangle.x + rectangle.width, //
                  rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2);
              geometricLayer.popMatrix();
              ++index;
            }
          }
        }
        ORIGIN_RENDER_0.show(geodesicDisplay::matrixLift, shape.multiply(RealScalar.of(1.2)), Tensors.of(origin)).render(geometricLayer, graphics);
        POINTS_RENDER_0.show(geodesicDisplay::matrixLift, shape, controlPointsAll.extract(1, controlPointsAll.length())).render(geometricLayer, graphics);
      }
    }
  }

  public static void main(String[] args) {
    new Se2CoveringAnimationDemo().setVisible(1200, 600);
  }
}
