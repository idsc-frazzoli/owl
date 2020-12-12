// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import ch.ethz.idsc.java.awt.RenderQuality;

public class UbongoPrintable implements Printable {
  @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
    Graphics2D graphics = (Graphics2D) g;
    RenderQuality.setQuality(graphics);
    graphics.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    // graphics.scale(0.735, 0.735);
    if (pageIndex < UbongoPublish.values().length) {
      UbongoViewer.draw(graphics, UbongoPublish.values()[pageIndex]);
      return Printable.PAGE_EXISTS;
    }
    return Printable.NO_SUCH_PAGE;
  }

  public static void main(String[] args) {
    new UbongoPrintable();
    PrinterJob job = PrinterJob.getPrinterJob();
    PageFormat pageFormat = job.defaultPage();
    Paper paper = pageFormat.getPaper();
    // paper.setSize(11.7 * 72, 8.3 * 72);
    // pageFormat.setPaper(paper);
    pageFormat.setOrientation(PageFormat.LANDSCAPE);
    job.setPrintable(new UbongoPrintable(), pageFormat);
    boolean doPrint = job.printDialog();
    if (doPrint) {
      try {
        job.print();
      } catch (PrinterException e) {
        // The job did not successfully
        // complete
      }
    }
  }
}
