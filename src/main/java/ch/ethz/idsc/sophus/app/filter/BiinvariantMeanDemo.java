// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.fit.HsWeiszfeldMethod;
import ch.ethz.idsc.sophus.fit.SpatialMedian;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.Se2CoveringDisplay;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.Biinvariants;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ class BiinvariantMeanDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_FILL = ColorDataLists._097.cyclic().deriveWithAlpha(182);
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  final SpinnerLabel<Biinvariant> spinnerDistances = SpinnerLabel.of(Biinvariants.values());
  private final JToggleButton axes = new JToggleButton("axes");
  private final JToggleButton median = new JToggleButton("median");

  public BiinvariantMeanDemo() {
    super(true, GeodesicDisplays.SE2C_SE2_S2_H2_R2);
    spinnerDistances.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "pseudo distances");
    timerFrame.jToolBar.add(axes);
    {
      median.setSelected(true);
      timerFrame.jToolBar.add(median);
    }
    Distribution dX = UniformDistribution.of(-3, 3);
    Distribution dY = NormalDistribution.of(0, .3);
    Distribution dA = NormalDistribution.of(1, .5);
    Tensor tensor = Tensor.of(Array.of(l -> Tensors.of( //
        RandomVariate.of(dX), RandomVariate.of(dY), RandomVariate.of(dA)), 10).stream() //
        .map(Se2CoveringExponential.INSTANCE::exp));
    setControlPointsSe2(tensor);
    setGeodesicDisplay(Se2CoveringDisplay.INSTANCE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (axes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor sequence = getGeodesicControlPoints();
    int length = sequence.length();
    if (0 == length)
      return;
    Tensor weights = ConstantArray.of(RationalScalar.of(1, length), length);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    Tensor mean = biinvariantMean.mean(sequence, weights);
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.setStroke(STROKE);
    RenderQuality.setQuality(graphics);
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    for (Tensor point : sequence) {
      Tensor curve = Subdivide.of(0, 1, 20).map(geodesicInterface.curve(point, mean));
      Path2D path2d = geometricLayer.toPath2D(curve);
      graphics.draw(path2d);
    }
    graphics.setStroke(new BasicStroke(1));
    renderControlPoints(geometricLayer, graphics);
    {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      path2d.closePath();
      graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(0));
      graphics.fill(path2d);
      graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(0));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    if (median.isSelected()) {
      Biinvariant biinvariant = spinnerDistances.getValue();
      TensorUnaryOperator weightingInterface = //
          biinvariant.weighting(geodesicDisplay.hsManifold(), InversePowerVariogram.of(1), sequence);
      SpatialMedian spatialMedian = HsWeiszfeldMethod.of(biinvariantMean, weightingInterface, Chop._05);
      Optional<Tensor> optional = spatialMedian.uniform(sequence);
      if (optional.isPresent()) {
        mean = optional.get();
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
        Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape().multiply(RealScalar.of(0.7)));
        path2d.closePath();
        graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(1));
        graphics.fill(path2d);
        graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(1));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
  }

  public static void main(String[] args) {
    new BiinvariantMeanDemo().setVisible(1200, 600);
  }
}
