// code by jph
package ch.ethz.idsc.sophus.app.decim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.decim.CurveDecimation;
import ch.ethz.idsc.sophus.decim.LineDistances;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class BulkDecimationDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

  public BulkDecimationDemo() {
    super(true, GeodesicDisplays.SE2_R2);
    Distribution dX = UniformDistribution.of(-3, 3);
    Distribution dY = NormalDistribution.of(0, .3);
    Distribution dA = NormalDistribution.of(1, .5);
    Tensor tensor = Tensor.of(Array.of(l -> Tensors.of( //
        RandomVariate.of(dX), RandomVariate.of(dY), RandomVariate.of(dA)), 4).stream() //
        .map(Se2CoveringExponential.INSTANCE::exp));
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor sequence = getGeodesicControlPoints();
    int length = sequence.length();
    if (0 == length)
      return;
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.setStroke(STROKE);
    RenderQuality.setQuality(graphics);
    graphics.setStroke(new BasicStroke(1));
    renderControlPoints(geometricLayer, graphics);
    Tensor domain = Subdivide.of(0, 1, 10);
    {
      PathRender pathRender = new PathRender(COLOR_DATA_INDEXED_DRAW.getColor(0));
      for (int index = 1; index < sequence.length(); ++index) {
        Tensor tensor = domain.map(geodesicInterface.curve(sequence.get(index - 1), sequence.get(index)));
        pathRender.setCurve(tensor, false);
        pathRender.render(geometricLayer, graphics);
      }
    }
    CurveDecimation curveDecimation = CurveDecimation.of( //
        LineDistances.STANDARD.supply(geodesicDisplay.hsManifold()), //
        RealScalar.ONE);
    Tensor decimate = curveDecimation.apply(sequence);
    {
      PathRender pathRender = new PathRender(COLOR_DATA_INDEXED_DRAW.getColor(1));
      for (int index = 1; index < decimate.length(); ++index) {
        Tensor tensor = domain.map(geodesicInterface.curve(decimate.get(index - 1), decimate.get(index)));
        pathRender.setCurve(tensor, false);
        pathRender.render(geometricLayer, graphics);
      }
    }
  }

  public static void main(String[] args) {
    new BulkDecimationDemo().setVisible(1200, 600);
  }
}
