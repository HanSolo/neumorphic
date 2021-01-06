 /*
  * Copyright (c) 2020 by Gerrit Grunwald
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

 package eu.hansolo.fx.neumorphic;

 import eu.hansolo.fx.neumorphic.tools.NShape;
 import eu.hansolo.fx.neumorphic.tools.Helper;
 import javafx.beans.DefaultProperty;
 import javafx.beans.property.BooleanProperty;
 import javafx.beans.property.BooleanPropertyBase;
 import javafx.beans.property.ObjectProperty;
 import javafx.beans.property.ObjectPropertyBase;
 import javafx.beans.property.ReadOnlyBooleanProperty;
 import javafx.beans.property.StringProperty;
 import javafx.beans.property.StringPropertyBase;
 import javafx.collections.ObservableList;
 import javafx.event.ActionEvent;
 import javafx.event.EventHandler;
 import javafx.geometry.VPos;
 import javafx.scene.Node;
 import javafx.scene.canvas.Canvas;
 import javafx.scene.canvas.GraphicsContext;
 import javafx.scene.control.ContentDisplay;
 import javafx.scene.control.Control;
 import javafx.scene.effect.BlurType;
 import javafx.scene.effect.DropShadow;
 import javafx.scene.effect.InnerShadow;
 import javafx.scene.layout.Pane;
 import javafx.scene.layout.Region;
 import javafx.scene.paint.Color;
 import javafx.scene.text.Font;
 import javafx.scene.text.TextAlignment;


 @DefaultProperty("children")
 public class NButton extends Region {
     private static final double                                    PREFERRED_WIDTH  = 100;
     private static final double                                    PREFERRED_HEIGHT = 24;
     private static final double                                    MINIMUM_WIDTH    = 10;
     private static final double                                    MINIMUM_HEIGHT   = 10;
     private static final double                                    MAXIMUM_WIDTH    = 1024;
     private static final double                                    MAXIMUM_HEIGHT   = 1024;
     private static final double                                    OFFSET           = 0.5;
     private              double                                    size;
     private              double                                    width;
     private              double                                    height;
     private              NShape                                    nShape;
     private              Node                                      graphics;
     private              Pane                                      pane;
     private              Canvas                                    canvas;
     private              GraphicsContext                           ctx;
     private              String                                    _text;
     private              StringProperty                            text;
     private              Color                                     _backgroundColor;
     private              ObjectProperty<Color>                     backgroundColor;
     private              Color                                     _textColor;
     private              ObjectProperty<Color>                     textColor;
     private              Font                                      _font;
     private              ObjectProperty<Font>                      font;
     private              Color                                     pressedColor;
     private              Color                                     brightShadowColor;
     private              Color                                     darkShadowColor;
     private              ContentDisplay                            contentDisplay;
     private              ObjectProperty<EventHandler<ActionEvent>> onAction;
     private              BooleanProperty                           armed;
     private              BooleanProperty                           pressed;
     private              BooleanProperty                           hover;
     private              BooleanProperty                           hoverable;
     private              double                                    cornerRadius;
     private              double                                    shadowRadius;
     private              double                                    shadowOffset;
     private              DropShadow                                outerShadow;
     private              InnerShadow                               innerShadow;


     // ******************** Constructors **************************************
     public NButton() {
         this("");
     }
     public NButton(final String text) {
         nShape            = NShape.RECTANGULAR;
         graphics          = null;
         _text             = text;
         _backgroundColor  = Color.web("#e2e6e8");
         _textColor        = Color.web("#6c737c");
         _font             = Font.font(10);
         pressedColor      = Helper.derive(_backgroundColor, Helper.isBright(_backgroundColor) ? 1.0125 : 1.05);
         brightShadowColor = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 1.1), 0.5);
         darkShadowColor   = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 0.9), 0.5);
         contentDisplay    = ContentDisplay.LEFT;
         onAction          = new ObjectPropertyBase<>() {
             @Override protected void invalidated() { setEventHandler(ActionEvent.ACTION, get()); }
             @Override public Object getBean() { return NButton.this; }
             @Override public String getName() { return "onAction"; }
         };
         armed             = new BooleanPropertyBase(false) {
             @Override protected void invalidated() {}
             @Override public Object getBean() { return NButton.this; }
             @Override public String getName() { return "armed"; }
         };
         pressed           = new BooleanPropertyBase(false) {
             @Override protected void invalidated() {
                 setArmed(get());
                 if (!get()) { fire(); }
                 layoutChildren();
             }
             @Override public Object getBean() { return NButton.this; }
             @Override public String getName() { return "pressed"; }
         };
         hover             = new BooleanPropertyBase(true) {
             @Override protected void invalidated() { redraw(); }
             @Override public Object getBean() { return NButton.this; }
             @Override public String getName() { return "hover"; }
         };
         hoverable         = new BooleanPropertyBase(false) {
             @Override protected void invalidated() {
                 hover.set(get() ? false : true);
                 redraw();
             }
             @Override public Object getBean() { return NButton.this; }
             @Override public String getName() { return "hoverable"; }
         };
         cornerRadius      = 5;
         shadowRadius      = 6;
         shadowOffset      = 2;
         outerShadow       = new DropShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
         outerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));
         innerShadow       = new InnerShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
         innerShadow.setInput(new InnerShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));
         initGraphics();
         registerListeners();
     }


     // ******************** Initialization ************************************
     private void initGraphics() {
         if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
             Double.compare(getHeight(), 0.0) <= 0) {
             if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                 setPrefSize(getPrefWidth(), getPrefHeight());
             } else {
                 setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
             }
         }

         canvas = new Canvas(getPrefWidth(), getPrefHeight());
         ctx    = canvas.getGraphicsContext2D();
         ctx.setTextAlign(TextAlignment.CENTER);
         ctx.setTextBaseline(VPos.CENTER);

         pane = new Pane(canvas);

         getChildren().setAll(pane);
     }

     private void registerListeners() {
         widthProperty().addListener(o -> resize());
         heightProperty().addListener(o -> resize());
         canvas.setOnMouseEntered(e -> { if (hoverable.get()) { hover.set(true); } });
         canvas.setOnMouseExited(e -> { if (hoverable.get()) { hover.set(false); } });
         canvas.setOnMousePressed(e -> pressed.set(true));
         canvas.setOnMouseReleased(e -> pressed.set(false));
     }


     // ******************** Methods *******************************************
     @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
     @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
     @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
     @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
     @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
     @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

     @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

     public String getText() { return null == text ? _text : text.get(); }
     public void setText(final String text) {
         if (null == this.text) {
             _text = text;
             redraw();
         } else {
             this.text.set(text);
         }
     }
     public StringProperty textProperty() {
         if (null == text) {
             text = new StringPropertyBase(_text) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return NButton.this; }
                 @Override public String getName() { return "text"; }
             };
             _text = null;
         }
         return text;
     }
     
     public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
     public void setBackgroundColor(final Color backgroundColor) {
         if (null == this.backgroundColor) {
             boolean isBright  = Helper.isBright(backgroundColor);
             _backgroundColor  = backgroundColor;
             pressedColor      = Helper.derive(_backgroundColor, isBright ? 1.0125 : 1.05);
             brightShadowColor = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, isBright ? 1.1 : 1.3), isBright ? 0.5 : 1.0);
             darkShadowColor   = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, isBright ? 0.9 : 0.7), isBright ? 0.5 : 1.0);
             resize();
         } else {
             this.backgroundColor.set(backgroundColor);
         }
     }
     public ObjectProperty<Color> backgroundColorProperty() {
         if (null == backgroundColor) {
             backgroundColor = new ObjectPropertyBase<>(_backgroundColor) {
                 @Override protected void invalidated() {
                     boolean isBright  = Helper.isBright(get());
                     pressedColor      = Helper.derive(get(), isBright ? 1.0125 : 1.05);
                     brightShadowColor = Helper.getColorWithOpacity(Helper.derive(get(), isBright ? 1.1 : 1.3), isBright ? 0.5 : 1.0);
                     darkShadowColor   = Helper.getColorWithOpacity(Helper.derive(get(), isBright ? 0.9 : 0.7), isBright ? 0.5 : 1.0);
                     resize();
                 }
                 @Override public Object getBean() { return NButton.this; }
                 @Override public String getName() { return "backgroundColor"; }
             };
             _backgroundColor = null;
         }
         return backgroundColor;
     }

     public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
     public void setTextColor(final Color textColor) {
         if (null == this.textColor) {
             _textColor = textColor;
             redraw();
         } else {
             this.textColor.set(textColor);
         }
     }
     public ObjectProperty<Color> textColorProperty() {
         if (null == textColor) {
             textColor = new ObjectPropertyBase<Color>(_textColor) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return NButton.this; }
                 @Override public String getName() { return "textColor"; }
             };
             _textColor = null;
         }
         return textColor;
     }

     public Font getFont() { return null == font ? _font : font.get(); }
     public void setFont(final Font font) {
         if (null == this.font) {
             _font = font;
             redraw();
         } else {
             this.font.set(font);
         }
     }
     public ObjectProperty<Font> fontProperty() {
         if (null == font) {
             font = new ObjectPropertyBase<Font>(_font) {
                 @Override protected void invalidated() { redraw(); }
                 @Override public Object getBean() { return NButton.this; }
                 @Override public String getName() { return "font"; }
             };
             _font = null;
         }
         return font;
     }

     public ContentDisplay getContentDisplay() { return contentDisplay; }
     public void setContentDisplay(final ContentDisplay contentDisplay) {
         this.contentDisplay = contentDisplay;
         resize();
     }

     public boolean isHoverable() { return hoverable.get(); }
     public void setHoverable(final boolean hoverable) { this.hoverable.set(hoverable); }
     public BooleanProperty hoverableProperty() { return hoverable; }

     public boolean isHovered() { return hover.get(); }
     public void setHovered(final boolean hovered) { this.hover.set(hovered); }
     public BooleanProperty hoveredProperty() { return hover; }

     public NShape getNShape() { return nShape; }
     public void setNShape(final NShape nShape) {
         this.nShape = nShape;
         resize();
     }

     public void setGraphics(final Node graphics) {
         if (null == graphics) {
             if (null == this.graphics) { return; }
             Helper.enableNode(this.graphics, false);
             getChildren().remove(this.graphics);
             this.graphics = graphics;
         } else {
             if (null != this.graphics) {
                 Helper.enableNode(this.graphics, false);
                 getChildren().remove(this.graphics);
             }
             this.graphics = graphics;
             if (!(this.graphics instanceof Control)) { this.graphics.setMouseTransparent(true); }
             getChildren().add(this.graphics);
             Helper.enableNode(this.graphics, true);
         }
         resize();
     }

     public boolean isArmed() { return armed.get(); }
     private void setArmed(final boolean armed) { this.armed.set(armed); }
     public ReadOnlyBooleanProperty armedProperty() { return armed; }

     public EventHandler<ActionEvent> getOnAction() { return onAction.get(); }
     public void setOnAction(final EventHandler<ActionEvent> onAction) { this.onAction.set(onAction); }
     public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }

     public void fire() {
         if (!isDisabled()) { fireEvent(new ActionEvent()); }
     }


     // ******************** Layout ********************************************
     @Override public void layoutChildren() {
         super.layoutChildren();
         if (null == this.graphics) {
             redraw();
             return;
         } else {
             double gW     = this.graphics.getLayoutBounds().getWidth();
             double gH     = this.graphics.getLayoutBounds().getHeight();
             double w      = NShape.CIRCULAR == nShape ? size : getWidth();
             double h      = NShape.CIRCULAR == nShape ? size : getHeight();
             double offset = pressed.get() ? OFFSET : 0;
             switch (contentDisplay) {
                 case TOP:
                     graphics.relocate((width - gW) * 0.5 + offset, shadowRadius * 1.5 + offset);
                     break;
                 case RIGHT:
                     graphics.relocate(w - shadowRadius * 1.5 + offset, (h - gH) * 0.5 + offset);
                     break;
                 case BOTTOM:
                     graphics.relocate((w - gW) * 0.5 + offset, h - gH - shadowRadius * 1.5 + offset);
                     break;
                 case LEFT:
                     graphics.relocate(shadowRadius * 1.5 + offset, (h - gH) * 0.5 + offset);
                     break;
                 case GRAPHIC_ONLY:
                 case CENTER:
                 default:
                     graphics.relocate((w - gW) * 0.5 + offset, (h - gH) * 0.5 + offset);
                     break;
             }
             redraw();
         }
     }

     protected void resize() {
         width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
         height = Helper.clamp(getFont().getSize() * 2, Double.MAX_VALUE, getHeight() - getInsets().getTop() - getInsets().getBottom());
         size   = width < height ? width : height;

         if (width > 0 && height > 0) {
             switch (nShape) {
                 case RECTANGULAR:
                     pane.setMinSize(width, height);
                     pane.setMaxSize(width, height);
                     pane.setPrefSize(width, height);
                     pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

                     canvas.setWidth(width);
                     canvas.setHeight(height);

                     cornerRadius = Helper.clamp(1, 10, 0.1 * size);
                     break;
                 case PILL:
                     pane.setMinSize(width, height);
                     pane.setMaxSize(width, height);
                     pane.setPrefSize(width, height);
                     pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

                     canvas.setWidth(width);
                     canvas.setHeight(height);

                     cornerRadius = Helper.clamp(1, size, size / 1.25);
                     break;
                 case CIRCULAR:
                     pane.setMinSize(width, height);
                     pane.setMaxSize(size, size);
                     pane.setPrefSize(size, size);
                     pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

                     canvas.setWidth(size);
                     canvas.setHeight(size);

                     cornerRadius = size < 1 ? 1 : size;
                     break;
             }

             shadowRadius = Helper.clamp(2, 6, 0.12 * size);
             shadowOffset = Helper.clamp(2, 6, 0.04 * size);

             outerShadow = new DropShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
             outerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

             innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
             innerShadow.setInput(new InnerShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

             redraw();
         }
     }

     protected void redraw() {
         ctx.clearRect(0, 0, width, height);
         boolean isHover        = hover.get();
         boolean isPressed      = pressed.get();
         double  shadowRadiusX2 = 2 * shadowRadius;
         ctx.save();
         ctx.setEffect(isPressed ? innerShadow : isHover ? outerShadow : null);
         ctx.setFill(isPressed ? pressedColor : getBackgroundColor());
         switch (nShape) {
             case RECTANGULAR:
                 if (isPressed) {
                     ctx.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                 } else {
                     ctx.fillRoundRect(shadowRadius, shadowRadius, width - shadowRadiusX2, height - shadowRadiusX2, cornerRadius, cornerRadius);
                 }
                 break;
             case PILL       :
                 if (isPressed) {
                     ctx.fillRoundRect(0, 0, width, height, size, size);
                 } else {
                     ctx.fillRoundRect(shadowRadius, shadowRadius, width - shadowRadiusX2, height - shadowRadiusX2, cornerRadius, cornerRadius);
                 }
                 break;
             case CIRCULAR:
                 if (isPressed) {
                     ctx.fillOval(0, 0, size, size);
                 } else {
                     ctx.fillOval(shadowRadius, shadowRadius, size - shadowRadiusX2, size - shadowRadiusX2);
                 }
                 break;
         }
         ctx.restore();
         if (ContentDisplay.GRAPHIC_ONLY != contentDisplay) {
             double offset = pressed.get() ? OFFSET : 0;
             ctx.setFill(getTextColor());
             ctx.setFont(getFont());
             switch (nShape) {
                 case RECTANGULAR:
                 case PILL       : ctx.fillText(getText(), width * 0.5 + offset, height * 0.5 + offset, (width - shadowRadiusX2) * 0.9); break;
                 case CIRCULAR   : ctx.fillText(getText(), size * 0.5 + offset, size * 0.5 + offset, (size - shadowRadiusX2) * 0.9); break;
             }
         }
     }
 }
