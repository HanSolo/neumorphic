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

import eu.hansolo.fx.neumorphic.tools.Helper;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


@DefaultProperty("children")
public class NTextField extends Region {
    private static final double                PREFERRED_WIDTH  = 120;
    private static final double                PREFERRED_HEIGHT = 24;
    private static final double                MINIMUM_WIDTH    = 10;
    private static final double                MINIMUM_HEIGHT   = 10;
    private static final double                MAXIMUM_WIDTH    = 1024;
    private static final double                MAXIMUM_HEIGHT   = 1024;
    private static       String                userAgentStyleSheet;
    private              double                size;
    private              double                width;
    private              double                height;
    private              TextField             textField;
    private              Pane                  pane;
    private              Canvas                canvas;
    private              GraphicsContext       ctx;
    private              String                _text;
    private              StringProperty        text;
    private              Color                 _backgroundColor;
    private              ObjectProperty<Color> backgroundColor;
    private              Color                 _textBackgroundColor;
    private              Color                 _textColor;
    private              ObjectProperty<Color> textColor;
    private              Color                 _selectedColor;
    private              ObjectProperty<Color> selectedColor;
    private              Color                 brightShadowColor;
    private              Color                 darkShadowColor;
    private              ContentDisplay        contentDisplay;
    private              double                cornerRadius;
    private              double                shadowRadius;
    private              double                shadowOffset;
    private              double                glowRadius;
    private              DropShadow            outerShadow;
    private              InnerShadow           innerShadow;


    // ******************** Constructors **************************************
    public NTextField() {
        this("");
    }
    public NTextField(final String text) {
        _text                = text;
        _backgroundColor     = Color.web("#e2e6e8");
        _textBackgroundColor = Helper.derive(_backgroundColor, Helper.isBright(_backgroundColor) ? 0.99 : 1.2);
        _textColor           = Color.web("#6c737c");
        _selectedColor       = Color.web("#236dee");
        brightShadowColor    = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 1.1), 0.5);
        darkShadowColor      = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 0.9), 0.5);
        contentDisplay       = ContentDisplay.LEFT;
        cornerRadius         = 5;
        shadowRadius         = 6;
        shadowOffset         = 2;
        glowRadius           = 10;
        outerShadow          = new DropShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
        outerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));
        innerShadow          = new InnerShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
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

        textField = new TextField(_text);
        textField.setFont(Font.font(10));
        textField.setStyle("-fx-highlight-fill: #236dee;");
        textField.setStyle("-fx-text-inner-color: " + Helper.colorToCss(_textColor));

        pane = new Pane(canvas, textField);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
    }


    // ******************** Methods *******************************************
    @Override public void layoutChildren() {
        super.layoutChildren();

        double gW = textField.getLayoutBounds().getWidth();
        double gH = textField.getLayoutBounds().getHeight();
        double w  = width;
        double h  = height;
        textField.relocate((w - gW) * 0.5, (h - gH) * 0.5);
        redraw();
    }

    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public String getText() { return textField.getText(); }
    public void setText(final String text) { textField.setText(text); }
    public StringProperty textProperty() { return textField.textProperty(); }

    public Color getBackgroundColor() { return null == backgroundColor ? _backgroundColor : backgroundColor.get(); }
    public void setBackgroundColor(final Color backgroundColor) {
        if (null == this.backgroundColor) {
            boolean isBright     = Helper.isBright(backgroundColor);
            _backgroundColor     = backgroundColor;
            _textBackgroundColor = Helper.derive(_backgroundColor, Helper.isBright(_backgroundColor) ? 0.99 : 1.2);
            brightShadowColor    = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, isBright ? 1.1 : 1.3), isBright ? 0.5 : 1.0);
            darkShadowColor      = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, isBright ? 0.9 : 0.7), isBright ? 0.5 : 1.0);
            resize();
        } else {
            this.backgroundColor.set(backgroundColor);
        }
    }
    public ObjectProperty<Color> backgroundColorProperty() {
        if (null == backgroundColor) {
            backgroundColor = new ObjectPropertyBase<>(_backgroundColor) {
                @Override protected void invalidated() {
                    boolean isBright     = Helper.isBright(get());
                    _textBackgroundColor = Helper.derive(get(), isBright ? 0.99 : 1.2);
                    brightShadowColor    = Helper.getColorWithOpacity(Helper.derive(get(), isBright ? 1.1 : 1.3), isBright ? 0.5 : 1.0);
                    darkShadowColor      = Helper.getColorWithOpacity(Helper.derive(get(), isBright ? 0.9 : 0.7), isBright ? 0.5 : 1.0);
                    resize();
                }
                @Override public Object getBean() { return NTextField.this; }
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
            textField.setStyle("-fx-text-inner-color: " + Helper.colorToCss(_textColor));
            redraw();
        } else {
            this.textColor.set(textColor);
        }
    }
    public ObjectProperty<Color> textColorProperty() {
        if (null == textColor) {
            textColor = new ObjectPropertyBase<Color>(_textColor) {
                @Override protected void invalidated() {
                    textField.setStyle("-fx-text-inner-color: " + Helper.colorToCss(get()));
                    redraw();
                }
                @Override public Object getBean() { return NTextField.this; }
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
            textField.setStyle("-fx-highlight-fill: " + Helper.colorToCss(_selectedColor));
            redraw();
        } else {
            this.selectedColor.set(selectedColor);
        }
    }
    public ObjectProperty<Color> selectedColorProperty() {
        if (null == selectedColor) {
            selectedColor = new ObjectPropertyBase<>(_selectedColor) {
                @Override protected void invalidated() {
                    textField.setStyle("-fx-highlight-fill: " + Helper.colorToCss(get()));
                    redraw();
                }
                @Override public Object getBean() { return NTextField.this; }
                @Override public String getName() { return "selectedColor"; }
            };
            _selectedColor = null;
        }
        return selectedColor;
    }

    public Font getFont() { return textField.getFont(); }
    public void setFont(final Font font) { textField.setFont(font); }
    public ObjectProperty<Font> fontProperty() { return textField.fontProperty(); }

    public ContentDisplay getContentDisplay() { return contentDisplay; }
    public void setContentDisplay(final ContentDisplay contentDisplay) {
        this.contentDisplay = contentDisplay;
        resize();
    }

    public TextField getEditor() { return textField; }

    @Override public String getUserAgentStylesheet() {
        if (null == userAgentStyleSheet) { userAgentStyleSheet = getClass().getResource("ntextfield.css").toExternalForm(); }
        return userAgentStyleSheet;
    }



    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMinSize(width, height);
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            canvas.setWidth(width);
            canvas.setHeight(height);

            textField.setPrefSize(width - size / 2.5, height);

            cornerRadius = size / 1.25;
            cornerRadius = cornerRadius < 1 ? 1 : cornerRadius;

            shadowRadius = Helper.clamp(2, Double.MAX_VALUE, 0.12 * size);
            shadowOffset = Helper.clamp(2, Double.MAX_VALUE, 0.04 * size);

            outerShadow = new DropShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
            outerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

            innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
            innerShadow.setInput(new InnerShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

            redraw();
        }
    }

    private void redraw() {
        ctx.clearRect(0, 0, width, height);
        double  shadowRadiusX2 = 2 * shadowRadius;
        ctx.save();
        ctx.setEffect(innerShadow);
        ctx.setFill(_textBackgroundColor);
        ctx.fillRoundRect(shadowRadius, shadowRadius, width - shadowRadiusX2, height - shadowRadiusX2, cornerRadius, cornerRadius);
        ctx.restore();
    }
}
