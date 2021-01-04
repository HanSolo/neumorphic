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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


@DefaultProperty("children")
public class NRadioButton extends NToggleButton {
    private static final double                      PREFERRED_WIDTH  = 120;
    private static final double                      PREFERRED_HEIGHT = 24;
    private static final double                      MINIMUM_WIDTH    = 10;
    private static final double                      MINIMUM_HEIGHT   = 10;
    private static final double                      MAXIMUM_WIDTH    = 1024;
    private static final double                      MAXIMUM_HEIGHT   = 1024;
    private static final double                      SPACER           = 5;
    private              double                      size;
    private              double                      width;
    private              double                      height;
    private              HBox                        pane;
    private              Canvas                      canvas;
    private              GraphicsContext             ctx;
    private              Label                       label;
    private              Color                       brightShadowColor;
    private              Color                       darkShadowColor;
    private              double                      cornerRadius;
    private              double                      shadowRadius;
    private              double                      shadowOffset;
    private              double                      glowRadius;
    private              DropShadow                  outerShadow;
    private              InnerShadow                 innerShadow;
    private              DropShadow                  glow;


    // ******************** Constructors **************************************
    public NRadioButton() {
        this("");
    }
    public NRadioButton(final String text) {
        label             = new Label(text);
        brightShadowColor = Helper.getColorWithOpacity(Helper.derive(getBackgroundColor(), 1.1), 0.5);
        darkShadowColor   = Helper.getColorWithOpacity(Helper.derive(getBackgroundColor(), 0.9), 0.5);
        cornerRadius      = 5;
        shadowRadius      = 6;
        shadowOffset      = 2;
        glowRadius        = 10;
        outerShadow       = new DropShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
        outerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));
        glow              = new DropShadow(BlurType.TWO_PASS_BOX, getSelectedColor(), glowRadius, 0.5, 0, 0);
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
                setPrefSize(PREFERRED_WIDTH + Label.USE_PREF_SIZE, PREFERRED_HEIGHT);
            }
        }

        canvas = new Canvas(getPrefHeight(), getPrefHeight());
        ctx    = canvas.getGraphicsContext2D();
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.CENTER);

        label.setFont(Font.font(10));
        label.setPrefWidth(Label.USE_COMPUTED_SIZE);

        pane = new HBox(SPACER, canvas, label);
        pane.setFillHeight(false);
        pane.setAlignment(Pos.CENTER_LEFT);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        canvas.setOnMousePressed(e -> fire());
        backgroundColorProperty().addListener(o -> {
            Color   backgroundColor = getBackgroundColor();
            boolean isBright        = Helper.isBright(backgroundColor);
            brightShadowColor = Helper.getColorWithOpacity(Helper.derive(backgroundColor, isBright ? 1.1 : 1.3), isBright ? 0.5 : 1.0);
            darkShadowColor   = Helper.getColorWithOpacity(Helper.derive(backgroundColor, isBright ? 0.9 : 0.7), isBright ? 0.5 : 1.0);
        });
        textColorProperty().addListener(o -> label.setTextFill(getTextColor()));
        fontProperty().addListener(o -> label.setFont(getFont()));
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public String getText() { return label.getText(); }
    public void setText(final String text) { label.setText(text); }
    public StringProperty textProperty() { return label.textProperty(); }

    @Override public void fire() {
        if (null == getToggleGroup() || !isSelected()) {
            super.fire();
        }
    }


    // ******************** Layout ********************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
        resize();
    }

    @Override protected void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = Helper.clamp(getFont().getSize() * 2, Double.MAX_VALUE, getHeight() - getInsets().getTop() - getInsets().getBottom());
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMinSize(width, height);
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);

            canvas.setWidth(size);
            canvas.setHeight(size);

            cornerRadius = Helper.clamp(1, Double.MAX_VALUE, size);

            shadowRadius = Helper.clamp(2, Double.MAX_VALUE, 0.12 * size);
            shadowOffset = Helper.clamp(2, Double.MAX_VALUE, 0.04 * size);

            outerShadow = new DropShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
            outerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

            innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
            innerShadow.setInput(new InnerShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

            Color glowColor = Helper.isBright(getBackgroundColor()) ? Helper.getColorWithOpacity(getSelectedColor(), 0.25) : getSelectedColor();
            glowRadius = Helper.clamp(4, Double.MAX_VALUE, size * 0.2);
            glow = new DropShadow(BlurType.TWO_PASS_BOX, glowColor, glowRadius, 0.0, 0, 0);

            redraw();
        }
    }

    @Override protected void redraw() {
        ctx.clearRect(0, 0, size, size);
        boolean isSelected     = isSelected();
        double  shadowRadiusX2 = 2 * shadowRadius;
        double  shadowRadiusX3 = 3 * shadowRadius;
        double  shadowRadiusX4 = 4 * shadowRadius;
        ctx.save();
        ctx.setEffect(isSelected ? innerShadow : outerShadow);
        ctx.setFill(getBackgroundColor());
        ctx.fillOval(shadowRadiusX2, shadowRadiusX2, size - shadowRadiusX4, size - shadowRadiusX4);
        ctx.restore();
        if (isSelected) {
            ctx.save();
            ctx.setEffect(glow);
            ctx.setFill(getSelectedColor());
            ctx.fillOval(shadowRadiusX3, shadowRadiusX3, size - shadowRadiusX3 * 2, size - shadowRadiusX3 * 2);
            ctx.restore();
        }
    }
}
