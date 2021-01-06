/*
 * Copyright (c) 2021 by Gerrit Grunwald
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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


@DefaultProperty("children")
public class NSwitch extends Region {
    public enum NSwitchStyle { NUMBER, TEXT }
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
    private              Pane                                      pane;
    private              Canvas                                    canvas;
    private              GraphicsContext                           ctx;
    private              Color                                     _backgroundColor;
    private              ObjectProperty<Color>                     backgroundColor;
    private              Color                                     _textColor;
    private              ObjectProperty<Color>                     textColor;
    private              Color                                     _selectedColor;
    private              ObjectProperty<Color>                     selectedColor;
    private              Font                                      _font;
    private              ObjectProperty<Font>                      font;
    private              Color                                     brightShadowColor;
    private              Color                                     darkShadowColor;
    private              ContentDisplay                            contentDisplay;
    private              ObjectProperty<EventHandler<ActionEvent>> onAction;
    private              BooleanProperty                           on;
    private              NSwitchStyle                              switchStyle;
    private              double                                    cornerRadius;
    private              double                                    shadowRadius;
    private              double                                    shadowOffset;
    private              double                                    glowRadius;
    private              DropShadow                                outerShadow;
    private              InnerShadow                               innerShadow;
    private              DropShadow                                glow;


    // ******************** Constructors **************************************
    public NSwitch() {
        nShape = NShape.PILL;
        _backgroundColor  = Color.web("#e2e6e8");
        _textColor        = Color.web("#6c737c");
        _selectedColor    = Color.web("#236dee");
        _font             = Font.font(10);
        brightShadowColor = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 1.1), 0.5);
        darkShadowColor   = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 0.9), 0.5);
        contentDisplay    = ContentDisplay.LEFT;
        onAction          = new ObjectPropertyBase<>() {
            @Override protected void invalidated() { setEventHandler(ActionEvent.ACTION, get()); }
            @Override public Object getBean() { return NSwitch.this; }
            @Override public String getName() { return "onAction"; }
        };
        on                = new BooleanPropertyBase(false) {
            @Override protected void invalidated() {
                fire();
                layoutChildren();
            }
            @Override public Object getBean() { return NSwitch.this; }
            @Override public String getName() { return "on"; }
        };
        switchStyle       = NSwitchStyle.TEXT;
        cornerRadius      = 5;
        shadowRadius      = 6;
        shadowOffset      = 2;
        glowRadius        = 10;
        outerShadow       = new DropShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
        outerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));
        glow              = new DropShadow(BlurType.TWO_PASS_BOX, _selectedColor, glowRadius, 0.5, 0, 0);
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
        canvas.setOnMousePressed(e -> checkState(e));
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
    public void setBackgroundColor(final Color backgroundColor) {
        if (null == this.backgroundColor) {
            boolean isBright  = Helper.isBright(backgroundColor);
            _backgroundColor  = backgroundColor;
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
                    brightShadowColor = Helper.getColorWithOpacity(Helper.derive(get(), isBright ? 1.1 : 1.3), isBright ? 0.5 : 1.0);
                    darkShadowColor   = Helper.getColorWithOpacity(Helper.derive(get(), isBright ? 0.9 : 0.7), isBright ? 0.5 : 1.0);
                    resize();
                }
                @Override public Object getBean() { return NSwitch.this; }
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
                @Override public Object getBean() { return NSwitch.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    public Color getSelectedColor() { return null == selectedColor ? _selectedColor : selectedColor.get(); }
    public void setSelectedColor(final Color selectedColor) {
        if (null == this.selectedColor) {
            _selectedColor = selectedColor;
            redraw();
        } else {
            this.selectedColor.set(selectedColor);
        }
    }
    public ObjectProperty<Color> selectedColorProperty() {
        if (null == selectedColor) {
            selectedColor = new ObjectPropertyBase<Color>(_selectedColor) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return NSwitch.this; }
                @Override public String getName() { return "selectedColor"; }
            };
            _selectedColor = null;
        }
        return selectedColor;
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
                @Override public Object getBean() { return NSwitch.this; }
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

    public NShape getNShape() { return nShape; }
    public void setNShape(final NShape nShape) {
        this.nShape = nShape;
        resize();
    }

    public boolean isOn() { return on.get(); }
    public void setOn(final boolean on) { this.on.set(on); }
    public BooleanProperty onProperty() { return on; }

    public NSwitchStyle getSwitchStyle() { return switchStyle; }
    public void setSwitchStyle(final NSwitchStyle switchStyle) {
        this.switchStyle = switchStyle;
        redraw();
    }

    public EventHandler<ActionEvent> getOnAction() { return onAction.get(); }
    public void setOnAction(final EventHandler<ActionEvent> onAction) { this.onAction.set(onAction); }
    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }

    public void fire() {
        if (!isDisabled()) { fireEvent(new ActionEvent()); }
    }

    private void checkState(final MouseEvent e) {
        setOn(e.getX() < width * 0.5);
    }


    // ******************** Layout ********************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
        redraw();
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

            Color glowColor = Helper.isBright(getBackgroundColor()) ? Helper.getColorWithOpacity(getSelectedColor(), 0.25) : getSelectedColor();
            glowRadius = Helper.clamp(4, 8, size * 0.2);
            glow       = new DropShadow(BlurType.TWO_PASS_BOX, glowColor, glowRadius, 0.0, 0, 0);

            redraw();
        }
    }

    protected void redraw() {
        ctx.clearRect(0, 0, width, height);
        double  shadowRadiusX2 = 2 * shadowRadius;
        ctx.save();
        ctx.setEffect(outerShadow);
        ctx.setFill(getBackgroundColor());
        switch (nShape) {
            case RECTANGULAR:
            case PILL       :
            default         : ctx.fillRoundRect(shadowRadius, shadowRadius, width - shadowRadiusX2, height - shadowRadiusX2, cornerRadius, cornerRadius); break;
        }
        ctx.restore();

        // Draw on / off
        double  innerButtonWidth = (width / 2) - shadowRadius;
        double  innerOffset      = shadowRadius + 2;
        double  innerRadius      = cornerRadius * 0.8;
        boolean isOn             = isOn();
        ctx.save();
        ctx.setFill(getBackgroundColor());
        InnerShadow shadow = new InnerShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset / 2, -shadowOffset / 2);
        shadow.setInput(new InnerShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset / 2, shadowOffset / 2));
        ctx.setEffect(shadow);
        if (isOn) {
            switch (nShape) {
                case RECTANGULAR:
                case PILL       :
                default         : ctx.fillRoundRect(innerOffset, innerOffset, innerButtonWidth, height - shadowRadiusX2 - 4, innerRadius, innerRadius);
            }
        } else {
            switch (nShape) {
                case RECTANGULAR:
                case PILL       :
                default         : ctx.fillRoundRect(width - innerOffset - innerButtonWidth, innerOffset, innerButtonWidth, height - shadowRadiusX2 - 4, innerRadius, innerRadius);
            }
        }
        ctx.restore();
        ctx.save();
        double offset;
        ctx.setFill(getTextColor());
        ctx.setFont(getFont());
        switch (nShape) {
            case RECTANGULAR:
            case PILL:
            default:
                offset = isOn ? OFFSET : 0;
                ctx.setFill(isOn ? getSelectedColor() : getTextColor());
                ctx.setEffect(isOn ? glow : null);
                ctx.fillText(NSwitchStyle.TEXT == switchStyle ? "ON" : "1", innerOffset + innerButtonWidth * 0.5 + offset, height * 0.5 + offset, innerButtonWidth);
                offset = isOn ? 0 : OFFSET;
                ctx.setEffect(isOn ? null : glow);
                ctx.setFill(isOn ? getTextColor() : getSelectedColor());
                ctx.fillText(NSwitchStyle.TEXT == switchStyle ? "OFF" : "0", width - innerOffset - innerButtonWidth * 0.5 + offset, height * 0.5 + offset, innerButtonWidth);
                break;
        }
        ctx.restore();
    }
}
