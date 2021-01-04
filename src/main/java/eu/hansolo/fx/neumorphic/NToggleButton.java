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

import eu.hansolo.fx.neumorphic.tools.ButtonShape;
import eu.hansolo.fx.neumorphic.tools.Helper;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


@DefaultProperty("children")
public class NToggleButton extends Region implements Toggle {
    private static final double                      PREFERRED_WIDTH  = 24;
    private static final double                      PREFERRED_HEIGHT = 24;
    private static final double                      MINIMUM_WIDTH    = 10;
    private static final double                      MINIMUM_HEIGHT   = 10;
    private static final double                      MAXIMUM_WIDTH    = 1024;
    private static final double                      MAXIMUM_HEIGHT   = 1024;
    private static final double                      OFFSET           = 0.5;
    private              double                      size;
    private              double                      width;
    private              double                      height;
    private              ButtonShape                 buttonShape;
    private              Node                        graphics;
    private              Pane                        pane;
    private              Canvas                      canvas;
    private              GraphicsContext             ctx;
    private              String                      _text;
    private              StringProperty              text;
    private              ObjectProperty<ToggleGroup> toggleGroup;
    private              Color                       _backgroundColor;
    private              ObjectProperty<Color>       backgroundColor;
    private              Color                       _textColor;
    private              ObjectProperty<Color>       textColor;
    private              Color                       _selectedColor;
    private              ObjectProperty<Color>       selectedColor;
    private              Font                        _font;
    private              ObjectProperty<Font>        font;
    private              Color                       pressedColor;
    private              Color                       brightShadowColor;
    private              Color                       darkShadowColor;
    private              ContentDisplay              contentDisplay;
    private              BooleanProperty             pressed;
    private              BooleanProperty             selected;
    private              double                      cornerRadius;
    private              double                      shadowRadius;
    private              double                      shadowOffset;
    private              double                      glowRadius;
    private              DropShadow                  outerShadow;
    private              InnerShadow                 innerShadow;
    private              DropShadow                  glow;


    // ******************** Constructors **************************************
    public NToggleButton() {
        this("");
    }
    public NToggleButton(final String text) {
        buttonShape       = ButtonShape.RECTANGULAR;
        toggleGroup       = new ObjectPropertyBase<>(null) {
            private ToggleGroup oldToggleGroup;
            @Override protected void invalidated() {
                final ToggleGroup toggleGroup = get();
                if (null != toggleGroup && !toggleGroup.getToggles().contains(NToggleButton.this)) {
                    if (oldToggleGroup != null) { oldToggleGroup.getToggles().remove(NToggleButton.this); }
                    toggleGroup.getToggles().add(NToggleButton.this);
                } else if (null == toggleGroup) {
                    oldToggleGroup.getToggles().remove(NToggleButton.this);
                }
                oldToggleGroup = toggleGroup;
            }
            @Override public Object getBean() { return NToggleButton.this; }
            @Override public String getName() { return "toggleGroup"; }
        };
        graphics          = null;
        _text             = text;
        _backgroundColor  = Color.web("#e2e6e8");
        _textColor        = Color.web("#6c737c");
        _selectedColor    = Color.web("#236dee");
        _font             = Font.font(10);
        pressedColor      = Helper.derive(_backgroundColor, Helper.isBright(_backgroundColor) ? 1.0125 : 1.05);
        brightShadowColor = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 1.1), 0.5);
        darkShadowColor   = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 0.9), 0.5);
        contentDisplay    = ContentDisplay.LEFT;
        pressed           = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { layoutChildren(); }
            @Override public Object getBean() { return NToggleButton.this; }
            @Override public String getName() { return "pressed"; }
        };
        selected          = new BooleanPropertyBase(false) {
            @Override protected void invalidated() {
                final boolean selected = get();
                final ToggleGroup toggleGroup = getToggleGroup();
                if (toggleGroup != null) {
                    if (selected) {
                        toggleGroup.selectToggle(NToggleButton.this);
                    } else if (toggleGroup.getSelectedToggle() == NToggleButton.this) {
                        toggleGroup.getSelectedToggle().setSelected(false);
                        toggleGroup.selectToggle(null);
                    }
                }
                layoutChildren();
            }
            @Override public Object getBean() { return NToggleButton.this; }
            @Override public String getName() { return "selected"; }
        };
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
        canvas.setOnMousePressed(e -> selected.set(selected.get() ? false : true));
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
                @Override public Object getBean() { return NToggleButton.this; }
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
                @Override public Object getBean() { return NToggleButton.this; }
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
                @Override public Object getBean() { return NToggleButton.this; }
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
                @Override public Object getBean() { return NToggleButton.this; }
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
                @Override public Object getBean() { return NToggleButton.this; }
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

    public boolean isSelected() { return selected.get(); }
    public void setSelected(final boolean selected) { this.selected.set(selected); }
    public BooleanProperty selectedProperty() { return selected; }

    public ButtonShape getButtonShape() { return buttonShape; }
    public void setButtonShape(final ButtonShape buttonShape) {
        switch(buttonShape) {
            case CIRCULAR: this.buttonShape = ButtonShape.PILL; break;
            default      : this.buttonShape = buttonShape; break;
        }
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

    public final ToggleGroup getToggleGroup() { return null == toggleGroup ? null : toggleGroup.get(); }
    public final void setToggleGroup(final ToggleGroup toggleGroup) { this.toggleGroup.set(toggleGroup); }
    public final ObjectProperty<ToggleGroup> toggleGroupProperty() { return toggleGroup; }

    public void fire() {
        if (!isDisabled()) {
            setSelected(!isSelected());
            fireEvent(new ActionEvent());
        }
    }


    // ******************** Layout ********************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
        if (null == this.graphics) {
            redraw();
            return;
        } else {
            graphics.setEffect(selected.get() ? glow : null);
            double gW     = this.graphics.getLayoutBounds().getWidth();
            double gH     = this.graphics.getLayoutBounds().getHeight();
            double w      = width;
            double h      = getHeight();
            double offset = (selected.get() || pressed.get()) ? OFFSET : 0;
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
            switch (buttonShape) {
                case RECTANGULAR:
                    pane.setMinSize(width, height);
                    pane.setMaxSize(width, height);
                    pane.setPrefSize(width, height);
                    pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

                    canvas.setWidth(width);
                    canvas.setHeight(height);

                    cornerRadius = 0.1 * size;
                    break;
                case PILL:
                    pane.setMinSize(width, height);
                    pane.setMaxSize(width, height);
                    pane.setPrefSize(width, height);
                    pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

                    canvas.setWidth(width);
                    canvas.setHeight(height);

                    cornerRadius = size / 1.25;
                    break;
                case CIRCULAR:
                    pane.setMinSize(width, height);
                    pane.setMaxSize(size, size);
                    pane.setPrefSize(size, size);
                    pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

                    canvas.setWidth(size);
                    canvas.setHeight(size);

                    cornerRadius = size;
                    break;
            }

            cornerRadius = cornerRadius < 1 ? 1 : cornerRadius;

            shadowRadius = Helper.clamp(2, Double.MAX_VALUE, 0.12 * size);
            shadowOffset = Helper.clamp(2, Double.MAX_VALUE, 0.04 * size);

            outerShadow = new DropShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
            outerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

            innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
            innerShadow.setInput(new InnerShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

            Color glowColor = Helper.isBright(getBackgroundColor()) ? Helper.getColorWithOpacity(getSelectedColor(), 0.25) : getSelectedColor();
            glowRadius = Helper.clamp(4, Double.MAX_VALUE, size * 0.2);
            glow       = new DropShadow(BlurType.TWO_PASS_BOX, glowColor, glowRadius, 0.0, 0, 0);

            redraw();
        }
    }

    protected void redraw() {
        ctx.clearRect(0, 0, width, height);
        boolean isSelected     = selected.get();
        boolean isPressed      = pressed.get();
        double  shadowRadiusX2 = 2 * shadowRadius;
        ctx.save();
        ctx.setEffect(isSelected ? innerShadow : outerShadow);
        ctx.setFill((isSelected || isPressed) ? pressedColor : getBackgroundColor());
        switch (buttonShape) {
            case RECTANGULAR:
            case PILL:
            default:
                ctx.fillRoundRect(shadowRadius, shadowRadius, width - shadowRadiusX2, height - shadowRadiusX2, cornerRadius, cornerRadius);
                break;
        }
        ctx.restore();
        if (ContentDisplay.GRAPHIC_ONLY != contentDisplay) {
            double offset = (selected.get() || pressed.get()) ? OFFSET : 0;
            ctx.setFill(isSelected ? getSelectedColor() : getTextColor());
            ctx.setFont(getFont());
            ctx.setEffect(isSelected ? glow : null);
            switch (buttonShape) {
                case RECTANGULAR:
                case PILL:
                default:
                    ctx.fillText(getText(), width * 0.5 + offset, height * 0.5 + offset, (width - shadowRadiusX2) * 0.9);
                    break;
            }
        }
    }
}