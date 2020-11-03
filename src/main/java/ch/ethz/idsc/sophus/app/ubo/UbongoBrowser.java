// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.img.ImageRotate;
import ch.ethz.idsc.tensor.io.ImageFormat;

/* package */ class UbongoBrowser extends AbstractDemo {
  private final UbongoBoard ubongoBoard;
  private final List<List<UbongoEntry>> list;
  private final SpinnerLabel<Integer> spinnerIndex = new SpinnerLabel<>();

  public UbongoBrowser(UbongoBoard ubongoBoard, List<List<UbongoEntry>> list) {
    this.ubongoBoard = ubongoBoard;
    this.list = list;
    spinnerIndex.setList(IntStream.range(0, list.size()).boxed().collect(Collectors.toList()));
    spinnerIndex.setIndex(0);
    spinnerIndex.addToComponentReduced(timerFrame.jToolBar, new Dimension(40, 28), "index");
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    List<UbongoEntry> solution = list.get(spinnerIndex.getIndex());
    {
      int scale = 30;
      List<Integer> size = Dimensions.of(ubongoBoard.mask);
      Tensor tensor = UbongoRender.of(size, solution);
      int pix = 50;
      int piy = 120;
      graphics.drawImage(ImageFormat.of(tensor), pix, piy, size.get(1) * scale, size.get(0) * scale, null);
    }
    int pix = 0;
    for (UbongoEntry ubongoEntry : solution) {
      UbongoEntry ubongoPiece = new UbongoEntry();
      ubongoPiece.stamp = ImageRotate.cw(ubongoEntry.ubongo.mask());
      ubongoPiece.ubongo = ubongoEntry.ubongo;
      List<Integer> size = Dimensions.of(ubongoPiece.stamp);
      Tensor tensor = UbongoRender.of(size, Arrays.asList(ubongoPiece));
      // List<Integer> size2 = Dimensions.of(tensor);
      int scale = 15;
      int piw = size.get(1) * scale;
      graphics.drawImage(ImageFormat.of(tensor), 30 + pix, 30, piw, size.get(0) * scale, null);
      pix += piw + 20;
    }
  }

  public static void main(String[] args) {
    UbongoBoards ubongoBoards = UbongoBoards.SHOTGUN1;
    List<List<UbongoEntry>> list = // ubongoBoards.solve();
        UbongoLoader.INSTANCE.load(ubongoBoards);
    if (list.isEmpty()) {
      System.err.println("no solutions");
    } else {
      UbongoBrowser ubongoBrowser = new UbongoBrowser(ubongoBoards.board(), list);
      ubongoBrowser.setVisible(800, 600);
    }
  }
}
