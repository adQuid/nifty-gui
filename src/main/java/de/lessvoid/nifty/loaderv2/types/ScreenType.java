package de.lessvoid.nifty.loaderv2.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.newdawn.slick.util.Log;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputMapping;
import de.lessvoid.nifty.layout.LayoutPart;
import de.lessvoid.nifty.loaderv2.NiftyFactory;
import de.lessvoid.nifty.loaderv2.NiftyLoader;
import de.lessvoid.nifty.loaderv2.types.helper.CollectionLogger;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.StopWatch;
import de.lessvoid.nifty.tools.StringHelper;
import de.lessvoid.nifty.tools.TimeProvider;
import de.lessvoid.xml.tools.ClassHelper;

public class ScreenType extends XmlBaseType {
  private Collection < LayerType > layers = new ArrayList < LayerType >();

  public void addLayer(final LayerType layer) {
    layers.add(layer);
  }

  public String output(final int offset) {
    return
      StringHelper.whitespace(offset)
      + "<screen> "
      + super.output(offset)
      + "\n" + CollectionLogger.out(offset + 1, layers, "layers");
  }

  public void create(
      final Nifty nifty,
      final NiftyType niftyType,
      final TimeProvider timeProvider) {
    String controller = getAttributes().get("controller");
    ScreenController screenController = nifty.findScreenController(controller);
    if (screenController == null) {
      screenController = ClassHelper.getInstance(controller, ScreenController.class);
    }
    
    String id = getAttributes().get("id");
    Screen screen = new Screen(nifty, id, screenController, timeProvider);
    screen.setDefaultFocusElement(getAttributes().get("defaultFocusElement"));

    String inputMappingClass = getAttributes().get("inputMapping");
    if (inputMappingClass != null) {
      NiftyInputMapping inputMapping = ClassHelper.getInstance(inputMappingClass, NiftyInputMapping.class);
      if (!(screenController instanceof KeyInputHandler)) {
        Log.warn("class [" + controller + "] tries to use inputMapping [" + inputMappingClass + "] but does not implement [" + KeyInputHandler.class.getName() + "]");
      } else {
        screen.addKeyboardInputHandler(inputMapping, KeyInputHandler.class.cast(screenController));
      }
    }

    Element rootElement = NiftyFactory.createRootLayer("root", nifty, screen, timeProvider);
    screen.setRootElement(rootElement);

    StopWatch stopWatch = new StopWatch(timeProvider);
    stopWatch.start();
    for (LayerType layerType : layers) {
      layerType.prepare(nifty, rootElement.getElementType());
    }
    Logger.getLogger(NiftyLoader.class.getName()).info("internal prepare screen (" + id + ") [" + stopWatch.stop() + "]");

    stopWatch.start();
    for (LayerType layerType : layers) {
      LayoutPart layerLayout = NiftyFactory.createRootLayerLayoutPart(nifty);
      screen.addLayerElement(
          layerType.create(
              rootElement,
              nifty,
              screen,
              layerLayout));
    }
    Logger.getLogger(NiftyLoader.class.getName()).info("internal create screen (" + id + ") [" + stopWatch.stop() + "]");

    screen.processAddAndRemoveLayerElements();
    nifty.addScreen(id, screen);
  }
}