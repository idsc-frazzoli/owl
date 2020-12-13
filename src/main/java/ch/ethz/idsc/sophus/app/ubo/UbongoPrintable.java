// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class UbongoPrintable implements Printable {
  private final int scale;
  private final double factor;

  public UbongoPrintable(int scale, double factor) {
    this.scale = scale;
    this.factor = factor;
  }

  @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
    // System.out.println(pageIndex);
    // Paper paper = pageFormat.getPaper();
    // System.out.println("x=" + paper.getImageableX());
    // System.out.println("y=" + paper.getImageableY());
    // System.out.println("w=" + paper.getImageableWidth());
    // System.out.println("h=" + paper.getImageableHeight());
    Graphics2D graphics = (Graphics2D) g;
    graphics.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    graphics.scale(factor, factor);
    if (pageIndex < Math.min(UbongoPublish.values().length, 200)) {
      UbongoViewer.draw(graphics, UbongoPublish.values()[pageIndex], scale);
      return Printable.PAGE_EXISTS;
    }
    return Printable.NO_SUCH_PAGE;
  }

  @Override
  public String toString() {
    return String.format("ubongo%2d_%04d", scale, (int) (factor * 1000));
  }

  public static void main(String[] args) {
    for (double factor : new double[] { 1.0, 0.995, 0.990943 })
      for (int scale : new int[] { 45, 46, 47, 48 }) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat pageFormat = printerJob.defaultPage();
        Paper paper = pageFormat.getPaper();
        paper.setImageableArea(0.5 * 72, 0.5 * 72, 7 * 72, 10.5 * 72);
        // paper.setSize(11.7 * 72, 8.3 * 72);
        pageFormat.setPaper(paper);
        pageFormat.setOrientation(PageFormat.LANDSCAPE);
        UbongoPrintable ubongoPrintable = new UbongoPrintable(scale, factor);
        printerJob.setPrintable(ubongoPrintable, pageFormat);
        printerJob.setJobName(ubongoPrintable.toString());
        try {
          printerJob.print();
        } catch (PrinterException printerException) {
          printerException.printStackTrace();
        }
      }
  }
}
