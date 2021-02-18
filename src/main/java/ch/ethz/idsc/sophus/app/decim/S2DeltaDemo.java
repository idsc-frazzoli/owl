// code by jph
package ch.ethz.idsc.sophus.app.decim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.hs.sn.S2Loxodrome;
import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.hs.sn.SnPerturbation;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.sca.win.HannWindow;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/* package */ class S2DeltaDemo extends GeodesicDisplayDemo {
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final int WIDTH = 360;
  private static final int HEIGHT = 240;
  // ---
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  // ---
  private SnDeltaContainer snDeltaRaw;
  private SnDeltaContainer snDeltaFil;

  public S2DeltaDemo() {
    super(GeodesicDisplays.S2_ONLY);
    shuffle();
  }

  private void shuffle() {
    ScalarTensorFunction stf = S2Loxodrome.of(.3);
    Tensor domain = Subdivide.of(0, 10, 255);
    TensorUnaryOperator tuo = SnPerturbation.of(NormalDistribution.of(0, 0.01));
    Tensor sequence = Tensor.of(domain.stream().map(Scalar.class::cast).map(stf).map(tuo));
    ScalarUnaryOperator window = HannWindow.FUNCTION;
    snDeltaRaw = new SnDeltaContainer(sequence, window);
    TensorUnaryOperator tensorUnaryOperator = CenterFilter.of( //
        GeodesicCenter.of(SnGeodesic.INSTANCE, WindowFunctions.GAUSSIAN.get()), 7);
    snDeltaFil = new SnDeltaContainer(tensorUnaryOperator.apply(sequence), window);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = geodesicDisplay();
    Tensor planar = Tensor.of(snDeltaRaw.sequence.stream().map(geodesicDisplay::toPoint));
    pathRenderCurve.setCurve(planar, false).render(geometricLayer, graphics);
    for (Tensor ctrl : snDeltaRaw.differences) {
      Tensor p = ctrl.get(0); // point
      Tensor v = ctrl.get(1); // vector
      {
        graphics.setStroke(new BasicStroke(1.5f));
        graphics.setColor(Color.GRAY);
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
        graphics.draw(geometricLayer.toLine2D(geodesicDisplay.tangentProjection(p).apply(v)));
        geometricLayer.popMatrix();
      }
    }
    if (false) { // moving a single tangent vector along
      Tensor v0 = UnitVector.of(3, 1).multiply(RealScalar.of(0.5));
      for (int index = 1; index < snDeltaRaw.sequence.length(); ++index) {
        Tensor p = snDeltaRaw.sequence.get(index - 1);
        {
          graphics.setStroke(new BasicStroke(1.5f));
          graphics.setColor(Color.RED);
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
          graphics.draw(geometricLayer.toLine2D(geodesicDisplay.tangentProjection(p).apply(v0)));
          geometricLayer.popMatrix();
        }
        Tensor q = snDeltaRaw.sequence.get(index - 0);
        v0 = SnManifold.INSTANCE.endomorphism(p, q).dot(v0);
      }
    }
    int mag = 4;
    {
      int ofs = 0;
      snDeltaRaw.jFreeChart.draw(graphics, new Rectangle(ofs, 0, WIDTH, HEIGHT));
      BufferedImage bufferedImage = snDeltaRaw.bufferedImage[0];
      graphics.drawImage(bufferedImage, ofs, HEIGHT, bufferedImage.getWidth() * mag, bufferedImage.getHeight() * mag, null);
    }
    {
      int ofs = WIDTH;
      snDeltaFil.jFreeChart.draw(graphics, new Rectangle(ofs, 0, WIDTH, HEIGHT));
      BufferedImage bufferedImage = snDeltaFil.bufferedImage[0];
      graphics.drawImage(bufferedImage, ofs, HEIGHT, bufferedImage.getWidth() * mag, bufferedImage.getHeight() * mag, null);
    }
  }

  public static void main(String[] args) {
    new S2DeltaDemo().setVisible(1000, 800);
  }
}
