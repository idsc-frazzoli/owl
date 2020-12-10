// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ArrayRender;
import ch.ethz.idsc.sophus.app.bdn.AveragedMovingDomain2D;
import ch.ethz.idsc.sophus.app.bdn.MovingDomain2D;
import ch.ethz.idsc.sophus.hs.Biinvariants;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ImageRotate;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/* package */ class UbongoViewer extends AbstractDemo {
  private static final int GRY = 128;
  private static final int MARGIN_X = 400;
  private static final int MARGIN_Y = 13;
  // 61.1465
  private static final int SCALE = 62;
  private static final int ZCALE = 12;
  private static final int MAX_X = 9;
  private static final int MAX_Y = 8;

  public static int maxWidth() {
    return MARGIN_X + MAX_X * SCALE + 1;
  }

  public static int maxHeight() {
    // TODO 300 is a magic const
    return MAX_Y * SCALE + MARGIN_Y * 2;
  }

  // ---
  private final SpinnerLabel<UbongoPublish> spinnerIndex = SpinnerLabel.of(UbongoPublish.values());

  public UbongoViewer() {
    spinnerIndex.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "index");
    spinnerIndex.setValue(UbongoPublish.CHRISTMT);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    draw(graphics, spinnerIndex.getValue());
  }

  

  public static void draw(Graphics2D graphics, UbongoPublish ubongoPublish) {
    {
      
      int maxHeight = maxHeight();
      GeometricLayer geometricLayer = GeometricLayer.of(Dot.of(Se2Matrix.flipY(maxHeight), DiagonalMatrix.of(maxHeight, maxHeight, 1)));
      int res = 20;
      Tensor dx = Subdivide.of(0.0, 2.0, res - 1);
      Tensor dy = Subdivide.of(0.0, 1.0, res - 1);
      Tensor domain = Tensors.matrix((cx, cy) -> Tensors.of(dx.get(cx), dy.get(cy)), dx.length(), dy.length());
      Tensor origin = Tensors.fromString("{{0,0}, {1,0}, {0,1}, {1,1},{2,0}}");
      TensorUnaryOperator tensorUnaryOperator = Biinvariants.METRIC.coordinate(RnManifold.INSTANCE, InversePowerVariogram.of(2), origin);
      MovingDomain2D movingDomain2D = AveragedMovingDomain2D.of(origin, tensorUnaryOperator, domain);
      Tensor target = Tensors.fromString("{{0,0}, {1,0.2}, {0,0.8},{2,4},{2.1,.2}}");
      Tensor[][] forward = movingDomain2D.forward(target, RnBiinvariantMean.INSTANCE);
      AxesRender.INSTANCE.render(geometricLayer, graphics);
      new ArrayRender(forward, ColorDataGradients.CLASSIC) //
          .render(geometricLayer, graphics);
    }
    //
    List<List<UbongoEntry>> solutions = UbongoLoader.INSTANCE.load(ubongoPublish.ubongoBoards);
    {
      UbongoBoard ubongoBoard = ubongoPublish.ubongoBoards.board();
      List<Integer> size = Dimensions.of(ubongoBoard.mask);
      Tensor tensor = ubongoBoard.mask.map(s -> Scalars.nonZero(s) ? RealScalar.of(GRY) : RealScalar.of(255));
      int pix = MARGIN_X;
      int piy = MARGIN_Y;
      graphics.drawImage(ImageFormat.of(tensor), pix, piy, size.get(1) * SCALE, size.get(0) * SCALE, null);
      drawGrid(graphics);
    }
    int piy = MARGIN_Y;
    int count = 0;
    graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
    for (int index : ubongoPublish.list) {
      ++count;
      graphics.setColor(Color.DARK_GRAY);
      int pix = 50;
      RenderQuality.setQuality(graphics);
      graphics.drawString("" + count, 2, piy + 20);
      RenderQuality.setDefault(graphics);
      List<UbongoEntry> solution = solutions.get(index);
      int scale = ZCALE;
      for (UbongoEntry ubongoEntry : solution) {
        UbongoEntry ubongoPiece = new UbongoEntry();
        ubongoPiece.stamp = ImageRotate.cw(ubongoEntry.ubongo.mask());
        ubongoPiece.ubongo = ubongoEntry.ubongo;
        List<Integer> size = Dimensions.of(ubongoPiece.stamp);
        Tensor tensor = UbongoRender.gray(size, Arrays.asList(ubongoPiece));
        int piw = size.get(1) * scale;
        graphics.drawImage(ImageFormat.of(tensor), pix, piy, piw, size.get(0) * scale, null);
        pix += piw + 2 * ZCALE;
      }
      piy += 4 * ZCALE + 2 * ZCALE;
    }
  }

  public static void drawGrid(Graphics graphics) {
    int pix = MARGIN_X;
    int piy = MARGIN_Y;
    graphics.setColor(Color.WHITE);
    for (int c = 0; c <= MAX_X; ++c)
      graphics.drawLine(pix + c * SCALE, piy, pix + c * SCALE, piy + MAX_Y * SCALE);
    for (int c = 0; c <= MAX_Y; ++c)
      graphics.drawLine(pix, piy + c * SCALE, pix + MAX_X * SCALE, piy + c * SCALE);
  }

  public static void main(String[] args) {
    UbongoViewer ubongoViewer = new UbongoViewer();
    ubongoViewer.setVisible(1200, 600);
  }
}
