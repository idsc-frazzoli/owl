// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TimerFrame extends BaseFrame {
  protected final Timer timer = new Timer();

  /** frame with repaint rate of 20[Hz] */
  public TimerFrame() {
    this(50, TimeUnit.MILLISECONDS);
  }

  /** @param period between repaint invocations */
  public TimerFrame(int period, TimeUnit timeUnit) {
    { // periodic task for rendering
      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          geometricComponent.jComponent.repaint();
        }
      };
      timer.schedule(timerTask, 100, TimeUnit.MILLISECONDS.convert(period, timeUnit));
    }
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        timer.cancel();
      }
    });
  }
}
