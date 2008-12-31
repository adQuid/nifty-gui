package de.lessvoid.nifty.loader.xpp3.elements;

import java.util.Properties;

import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;

/**
 * HoverType.
 * @author void
 */
public class HoverType {

  /**
   * width.
   */
  private String width;

  /**
   * height.
   */
  private String height;

  /**
   * falloffType.
   */
  private HoverFalloffType falloffType;

  /**
   * falloffConstraint.
   */
  private HoverFalloffConstraintType falloffConstraint;

  /**
   * copy constructor.
   * @param source source
   */
  public HoverType(final HoverType source) {
    this.width = source.width;
    this.height = source.height;
    this.falloffType = source.falloffType;
    this.falloffConstraint = source.falloffConstraint;
  }

  /**
   * default constructor.
   */
  public HoverType() {
  }

  /**
   * setWidth.
   * @param widthParam width
   */
  public void setWidth(final String widthParam) {
    this.width = widthParam;
  }

  /**
   * setHeight.
   * @param heightParam height
   */
  public void setHeight(final String heightParam) {
    this.height = heightParam;
  }

  /**
   * setFalloffType.
   * @param falloffTypeParam falloffType
   */
  public void setFalloffType(final HoverFalloffType falloffTypeParam) {
    this.falloffType = falloffTypeParam;
  }

  /**
   * setFalloffConstraint.
   * @param falloffConstraintParam falloffConstraint
   */
  public void setFalloffConstraint(final HoverFalloffConstraintType falloffConstraintParam) {
    this.falloffConstraint = falloffConstraintParam;
  }

  /**
   * init element.
   * @param element element
   */
  public Falloff buildFalloff(final Element element) {
    Properties prop = new Properties();
    if (width != null) {
      prop.put(Falloff.HOVER_WIDTH, width);
    }
    if (height != null) {
      prop.put(Falloff.HOVER_HEIGHT, height);
    }
    if (falloffType != null) {
      prop.put(Falloff.HOVER_FALLOFF_TYPE, falloffType.toString());
    }
    if (falloffConstraint != null) {
      prop.put(Falloff.HOVER_FALLOFF_CONSTRAINT, falloffConstraint.toString());
    }
    if (!prop.isEmpty()) {
      return new Falloff(prop);
    }
    return null;
  }
}
