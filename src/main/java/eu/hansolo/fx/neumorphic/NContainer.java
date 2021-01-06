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
import eu.hansolo.fx.neumorphic.tools.NShape;
import eu.hansolo.fx.neumorphic.tools.NStyle;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;


@DefaultProperty("children")
public class NContainer extends Region {
    private static final double                                    PREFERRED_WIDTH  = 100;
    private static final double                                    PREFERRED_HEIGHT = 100;
    private static final double                                    MINIMUM_WIDTH    = 10;
    private static final double                                    MINIMUM_HEIGHT   = 10;
    private static final double                                    MAXIMUM_WIDTH    = 2048;
    private static final double                                    MAXIMUM_HEIGHT   = 2048;
    private              double                                    size;
    private              double                                    width;
    private              double                                    height;
    private              NShape                                    nShape;
    private              StackPane                                 container;
    private              Rectangle                                 clip;
    private              Pane                                      pane;
    private              Canvas                                    canvas;
    private              GraphicsContext                           ctx;
    private              Color                                     _backgroundColor;
    private              ObjectProperty<Color>                     backgroundColor;
    private              Color                                     brightShadowColor;
    private              Color                                     darkShadowColor;
    private              NStyle                                    _style;
    private              ObjectProperty<NStyle>                    style;
    private              double                                    cornerRadius;
    private              double                                    shadowRadius;
    private              double                                    shadowRadiusX2;
    private              double                                    shadowOffset;
    private              DropShadow                                outerShadow;
    private              InnerShadow                               innerShadow;


    // ******************** Constructors **************************************
    public NContainer() {
        this("");
    }
    public NContainer(final String text) {
        nShape            = NShape.RECTANGULAR;
        _backgroundColor  = Color.web("#e2e6e8");
        brightShadowColor = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 1.1), 0.5);
        darkShadowColor   = Helper.getColorWithOpacity(Helper.derive(_backgroundColor, 0.9), 0.5);
        _style            = NStyle.EMBOSSED;
        cornerRadius      = 5;
        shadowRadius      = 6;
        shadowRadiusX2    = 12;
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

        clip = new Rectangle();

        container = new StackPane();
        container.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        container.setClip(clip);

        pane = new Pane(canvas, container);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width) { return MAXIMUM_HEIGHT; }

    public ObservableList<Node> getNChildren() { return container.getChildren(); }

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
                @Override public Object getBean() { return NContainer.this; }
                @Override public String getName() { return "backgroundColor"; }
            };
            _backgroundColor = null;
        }
        return backgroundColor;
    }

    public NStyle getNStyle() { return null == style ? _style : style.get(); }
    public void setNStyle(final NStyle style) {
        if (null == this.style) {
            _style = style;
            resize();
        } else {
            this.style.set(style);
        }
    }
    public ObjectProperty<NStyle> nStyleProperty() {
        if (null == style) {
            style = new ObjectPropertyBase<>(_style) {
                @Override protected void invalidated() { redraw(); }
                @Override public Object getBean() { return NContainer.this; }
                @Override public String getName() { return "style"; }
            };
            _style = null;
        }
        return style;
    }

    public NShape getNShape() { return nShape; }
    public void setNShape(final NShape nShape) {
        this.nShape = nShape;
        resize();
    }


    // ******************** Layout ********************************************
    @Override public void layoutChildren() {
        super.layoutChildren();
        resize();
    }

    protected void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
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

                    cornerRadius = Helper.clamp(1, 10, size / 1.25);
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

            shadowRadius   = Helper.clamp(2, 5, 0.12 * size);
            shadowRadiusX2 = shadowRadius * 2;
            shadowOffset   = Helper.clamp(2, 5, 0.04 * size);

            outerShadow = new DropShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
            outerShadow.setInput(new DropShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

            innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, brightShadowColor, shadowRadius, 0.5, -shadowOffset, -shadowOffset);
            innerShadow.setInput(new InnerShadow(BlurType.TWO_PASS_BOX, darkShadowColor, shadowRadius, 0.5, shadowOffset, shadowOffset));

            double containerWidth;
            double containerHeight;
            switch(nShape) {
                case CIRCULAR:
                    containerWidth  = size - shadowRadiusX2;
                    containerHeight = size - shadowRadiusX2;
                    clip.setArcWidth(height);
                    clip.setArcHeight(height);
                    break;
                case RECTANGULAR:
                case PILL:
                default:
                    containerWidth  = width  - shadowRadiusX2;
                    containerHeight = height - shadowRadiusX2;
                    clip.setArcWidth(cornerRadius);
                    clip.setArcHeight(cornerRadius);
                    break;
            }
            clip.setWidth(containerWidth);
            clip.setHeight(containerHeight);

            container.setMinSize(containerWidth, containerHeight);
            container.setMaxSize(containerWidth, containerHeight);
            container.setPrefSize(containerWidth, containerHeight);
            container.relocate(shadowRadius, shadowRadius);
            redraw();
        }
    }

    protected void redraw() {
        ctx.clearRect(0, 0, width, height);
        ctx.save();
        switch(getNStyle()) {
            case SUNKEN  : ctx.setEffect(innerShadow); break;
            case EMBOSSED: ctx.setEffect(outerShadow); break;
        }
        ctx.setFill(getBackgroundColor());
        switch (nShape) {
            case RECTANGULAR:
                if (NStyle.SUNKEN == getNStyle()) {
                    ctx.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);
                } else {
                    ctx.fillRoundRect(shadowRadius, shadowRadius, width - shadowRadiusX2, height - shadowRadiusX2, cornerRadius, cornerRadius);
                }
                break;
            case PILL       :
                if (NStyle.SUNKEN == getNStyle()) {
                    ctx.fillRoundRect(0, 0, width, height, height, height);
                } else {
                    ctx.fillRoundRect(shadowRadius, shadowRadius, width - shadowRadiusX2, height - shadowRadiusX2, height, height);
                }
                break;
            case CIRCULAR:
                if (NStyle.SUNKEN == getNStyle()) {
                    ctx.fillOval(0, 0, size, size);
                } else {
                    ctx.fillOval(shadowRadius, shadowRadius, size - shadowRadiusX2, size - shadowRadiusX2);
                }
                break;
        }
        ctx.restore();
    }
}
