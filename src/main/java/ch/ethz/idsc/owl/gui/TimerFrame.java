// code by jph
package ch.ethz.idsc.owl.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

public class TimerFrame extends BaseFrame {
  public final Timer timer = new Timer();

  public TimerFrame() {
    { // periodic task for rendering
      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          geometricComponent.jComponent.repaint();
        }
      };
      timer.schedule(timerTask, 100, 50);
    }
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        timer.cancel();
      }
    });
  }
}
