package de.lessvoid.nifty.effects;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.render.RenderStateType;
import de.lessvoid.nifty.tools.TimeProvider;

/**
 * manage all effects of an element.
 * @author void
 */
public class EffectManager {

  /**
   * all the effects.
   */
  private Map < EffectEventId, EffectProcessor > effects = new Hashtable < EffectEventId, EffectProcessor >();

  /**
   * alternateKey we should use.
   */
  private String alternateKey;

  /**
   * create a new effectManager with the given listener.
   */
  public EffectManager() {
    this.alternateKey = null;

    effects.put(EffectEventId.onStartScreen, new EffectProcessor(false, false));
    effects.put(EffectEventId.onEndScreen, new EffectProcessor(true, false));
    effects.put(EffectEventId.onFocus, new EffectProcessor(true, false));
    effects.put(EffectEventId.onClick, new EffectProcessor(false, false));
    effects.put(EffectEventId.onHover, new EffectProcessor(false, true));
    effects.put(EffectEventId.onActive, new EffectProcessor(true, false));
    effects.put(EffectEventId.onCustom, new EffectProcessor(false, false));
  }

  /**
   * register an effect.
   * @param id the id
   * @param e the effect
   */
  public final void registerEffect(final EffectEventId id, final Effect e) {
    effects.get(id).registerEffect(e);
  }

  /**
   * start all effects with the given id for the given element.
   * @param id the effect id to start
   * @param w the element
   * @param time TimeProvider
   * @param listener the {@link EndNotify} to use.
   */
  public final void startEffect(
      final EffectEventId id,
      final Element w,
      final TimeProvider time,
      final EndNotify listener) {
    ((EffectProcessor) effects.get(id)).activate(listener, alternateKey);
  }

  /**
   * prepare rendering.
   * @param renderDevice RenderDevice
   */
  public void begin(final NiftyRenderEngine renderDevice) {
    Set < RenderStateType > renderStates = RenderStateType.allStates();

    for (EffectProcessor processor : effects.values()) {
      renderStates.removeAll(processor.getRenderStatesToSave());
    }

    renderDevice.saveState(renderStates);
  }

  /**
   * finish rendering.
   * @param renderDevice RenderDevice
   */
  public void end(final NiftyRenderEngine renderDevice) {
    renderDevice.restoreState();
  }

  /**
   * render all pre effects.
   * @param renderDevice the renderDevice we should use.
   */
  public final void renderPre(final NiftyRenderEngine renderDevice) {
    effects.get(EffectEventId.onHover).renderPre(renderDevice);

    for (EffectProcessor processor : effects.values()) {
      if (!processor.isHoverEffect()) {
        processor.renderPre(renderDevice);
      }
    }
  }

  /**
   * render all post effects.
   * @param renderDevice the renderDevice we should use.
   */
  public final void renderPost(final NiftyRenderEngine renderDevice) {
    effects.get(EffectEventId.onHover).renderPost(renderDevice);

    for (EffectProcessor processor : effects.values()) {
      if (!processor.isHoverEffect()) {
        processor.renderPost(renderDevice);
      }
    }
  }

  /**
   * handle mouse hover effects.
   * @param element the current element
   * @param x mouse x position
   * @param y mouse y position
   */
  public void handleHover(final Element element, final int x, final int y) {
    EffectProcessor processor = effects.get(EffectEventId.onHover);
    processor.processHover(element, x, y);
  }

  /**
   * checks if a certain effect is active.
   * @param effectEventId the effectEventId to check
   * @return true, if active, false otherwise
   */
  public final boolean isActive(final EffectEventId effectEventId) {
    return effects.get(effectEventId).isActive();
  }

  /**
   * reset all effects.
   */
  public final void reset() {
    for (EffectProcessor processor : effects.values()) {
      processor.reset();
    }
  }

  /**
   * set the alternate key.
   * @param newAlternateKey alternate key
   */
  public void setAlternateKey(final String newAlternateKey) {
    this.alternateKey = newAlternateKey;
  }

  /**
   * get state string.
   * @param offset offset
   * @return String with state information
   */
  public String getStateString(final String offset) {
    StringBuffer data = new StringBuffer();

    int activeProcessors = 0;
    for (EffectEventId eventId : effects.keySet()) {
      EffectProcessor processor = effects.get(eventId);
      if (processor.isActive()) {
        activeProcessors++;

        data.append("\n" + offset);
        data.append("  {" + eventId.toString() + "} ");
        data.append(processor.getStateString());
      }
    }

    if (activeProcessors == 0) {
      return "";
    } else {
      return data.toString();
    }
  }

  /**
   * Stop effects with the given id.
   * @param effectId effect id to stop
   */
  public void stopEffect(final EffectEventId effectId) {
    ((EffectProcessor) effects.get(effectId)).setActive(false);
  }
}
