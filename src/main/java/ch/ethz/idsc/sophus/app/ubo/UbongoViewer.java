// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.ext.Cache;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ImageRotate;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.Import;

/* package */ class UbongoViewer extends AbstractDemo {
  private final SpinnerLabel<UbongoPublish> spinnerIndex = SpinnerLabel.of(UbongoPublish.values());
  private final Function<UbongoBoards, List<List<UbongoEntry>>> cache = Cache.of(this::compute, 100);

  public List<List<UbongoEntry>> compute(UbongoBoards ubongoBoards) {
    File folder = HomeDirectory.Documents("ubongo");
    folder.mkdir();
    File file = new File(folder, ubongoBoards.name());
    if (file.isFile())
      try {
        System.out.println("load");
        return Import.object(file);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    System.out.println("compute");
    List<List<UbongoEntry>> list = ubongoBoards.solve();
    try {
      Export.object(file, list);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return list;
  }

  public UbongoViewer() {
    spinnerIndex.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "index");
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    UbongoPublish ubongoPublish = spinnerIndex.getValue();
    List<List<UbongoEntry>> solutions = cache.apply(ubongoPublish.ubongoBoards);
    {
      UbongoBoard ubongoBoard = ubongoPublish.ubongoBoards.board();
      List<Integer> size = Dimensions.of(ubongoBoard.mask);
      Tensor tensor = ubongoBoard.mask.map(s -> Scalars.nonZero(s) ? RealScalar.of(192 + 16) : RealScalar.of(255));
      int scale = 40;
      graphics.drawImage(ImageFormat.of(tensor), 300, 50, size.get(1) * scale, size.get(0) * scale, null);
    }
    int piy = 10;
    int count = 0;
    graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
    for (int index : ubongoPublish.list) {
      ++count;
      graphics.setColor(Color.DARK_GRAY);
      int pix = 50;
      RenderQuality.setQuality(graphics);
      graphics.drawString("" + count, pix - 40, piy + 20);
      RenderQuality.setDefault(graphics);
      List<UbongoEntry> solution = solutions.get(index);
      int scale = 8;
      for (UbongoEntry ubongoEntry : solution) {
        UbongoEntry ubongoPiece = new UbongoEntry();
        ubongoPiece.stamp = ImageRotate.cw(ubongoEntry.ubongo.mask());
        ubongoPiece.ubongo = ubongoEntry.ubongo;
        List<Integer> size = Dimensions.of(ubongoPiece.stamp);
        Tensor tensor = UbongoRender.of(size, Arrays.asList(ubongoPiece));
        // List<Integer> size2 = Dimensions.of(tensor);
        int piw = size.get(1) * scale;
        graphics.drawImage(ImageFormat.of(tensor), pix, piy, piw, size.get(0) * scale, null);
        pix += piw + 20;
      }
      piy += 50;
    }
  }

  public static void main(String[] args) {
    UbongoViewer ubongoBrowser = new UbongoViewer();
    ubongoBrowser.setVisible(800, 600);
  }
}
