// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.img.ImageRotate;
import ch.ethz.idsc.tensor.io.ImageFormat;

/* package */ class UbongoViewer extends AbstractDemo {
  private static final int GRY = 128;
  private static final int MARGIN_X = 350;
  private static final int MARGIN_Y = 13;
  // 61.1465
  private static final int SCALE = 45;
  private static final int ZCALE = 9;
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
    UbongoBoard ubongoBoard = ubongoPublish.ubongoBoards.board();
    List<List<UbongoEntry>> solutions = UbongoLoader.INSTANCE.load(ubongoPublish.ubongoBoards);
    {
      List<Integer> size = Dimensions.of(ubongoBoard.mask);
      Tensor tensor = ubongoBoard.mask.map(s -> Scalars.nonZero(s) ? RealScalar.of(GRY) : RealScalar.of(255));
      int pix = MARGIN_X;
      int piy = MARGIN_Y;
      BufferedImage bufferedImage = ImageFormat.of(ImageResize.nearest(tensor, SCALE));
      graphics.drawImage(bufferedImage, pix, piy, null);
      drawGrid(graphics);
    }
    int piy = MARGIN_Y;
    int count = 0;
    graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
    for (int index : ubongoPublish.list) {
      ++count;
      graphics.setColor(Color.DARK_GRAY);
      int pix = 50;
      graphics.drawString("" + count, 2, piy + 20);
      List<UbongoEntry> solution = solutions.get(index);
      for (UbongoEntry ubongoEntry : solution) {
        UbongoEntry ubongoPiece = new UbongoEntry();
        ubongoPiece.stamp = ImageRotate.cw(ubongoEntry.ubongo.mask());
        ubongoPiece.ubongo = ubongoEntry.ubongo;
        List<Integer> size = Dimensions.of(ubongoPiece.stamp);
        Tensor tensor = UbongoRender.gray(size, Arrays.asList(ubongoPiece));
        int piw = size.get(1) * ZCALE;
        BufferedImage bufferedImage = ImageFormat.of(ImageResize.nearest(tensor, ZCALE));
        graphics.drawImage(bufferedImage, pix, piy, null);
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
