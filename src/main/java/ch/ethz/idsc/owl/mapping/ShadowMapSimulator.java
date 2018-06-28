// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.math.state.StateTime;

public class ShadowMapSimulator {
  private final Timer increaserTimer = new Timer("MapUpdateTimer");
  private final ShadowMap shadowMap;
  private final Supplier<StateTime> stateTimeSupplier;
  private boolean isPaused = false;

  public ShadowMapSimulator(ShadowMap shadowMap, Supplier<StateTime> stateTimeSupplier) {
    this.shadowMap = shadowMap;
    this.stateTimeSupplier = stateTimeSupplier;
  }

  public final void startNonBlocking(int updateRate) {
    TimerTask mapUpdate = new TimerTask() {
      @Override
      public void run() {
        if (!isPaused)
          shadowMap.updateMap(stateTimeSupplier.get(), 1.0f / updateRate);
      }
    };
    increaserTimer.scheduleAtFixedRate(mapUpdate, 10, 1000 / updateRate);
  }

  public final void flagShutdown() {
    increaserTimer.cancel();
  }

  public final void pause() {
    isPaused = true;
  }

  public final void resume() {
    isPaused = false;
  }
}
