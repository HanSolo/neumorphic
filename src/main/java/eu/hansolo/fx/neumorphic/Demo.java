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

 import eu.hansolo.fx.neumorphic.NButton.ButtonShape;
 import javafx.application.Application;
 import javafx.geometry.Insets;
 import javafx.scene.control.ContentDisplay;
 import javafx.scene.control.ToggleGroup;
 import javafx.scene.layout.Background;
 import javafx.scene.layout.BackgroundFill;
 import javafx.scene.layout.CornerRadii;
 import javafx.scene.layout.HBox;
 import javafx.scene.layout.VBox;
 import javafx.scene.paint.Color;
 import javafx.scene.text.Font;
 import javafx.stage.Stage;
 import javafx.scene.Scene;
 import org.kordamp.ikonli.antdesignicons.AntDesignIconsFilled;
 import org.kordamp.ikonli.javafx.FontIcon;


 /**
  * User: hansolo
  * Date: 30.12.20
  * Time: 06:56
  */
 public class Demo extends Application {
     private Color        brightBackground     = Color.web("#e5e8eb");
     private Color        brightForeground     = Color.web("#2e2f33");
     private Color        brightSelectionColor = Color.web("#CC00C1");
     private Color        darkBackground       = Color.web("#2e2f33");
     private Color        darkForeground       = Color.web("#e5e8eb");
     private Color        darkSelectionColor   = Color.web("#00FFF1");
     private Color        backgroundColor      = darkBackground;
     private Color        foregroundColor      = darkForeground;
     private Color        selectionColor       = darkSelectionColor;
     private NButton      button1;
     private NButton      button2;
     private NButton      button3;
     private NButton      button4;
     private NButton      button5;
     private NButton      button6;
     private NTextField   textField1;
     private NRadioButton radioButton1;
     private NRadioButton radioButton2;
     private NCheckBox    checkBox1;
     private NCheckBox    checkBox2;


     @Override public void init() {
         button1 = new NButton("Click");
         button1.setPrefSize(200, 80);
         button1.setFont(Font.font(24));
         button1.setButtonShape(ButtonShape.PILL);
         button1.setBackgroundColor(backgroundColor);
         button1.setSelectedColor(selectionColor);
         button1.setTextColor(foregroundColor);

         button2 = new NButton("Selectable");
         button2.setPrefSize(200, 80);
         button2.setFont(Font.font(24));
         button2.setSelectable(true);
         button2.setBackgroundColor(backgroundColor);
         button2.setTextColor(foregroundColor);
         button2.setSelectedColor(selectionColor);

         button3 = new NButton("1");
         button3.setPrefSize(80, 80);
         button3.setFont(Font.font(24));
         button3.setButtonShape(ButtonShape.CIRCULAR);
         button3.setBackgroundColor(backgroundColor);
         button3.setTextColor(foregroundColor);
         button3.setSelectedColor(selectionColor);

         button4 = new NButton("1");
         button4.setPrefSize(80, 80);
         button4.setFont(Font.font(24));
         button4.setSelectable(true);
         button4.setButtonShape(ButtonShape.CIRCULAR);
         button4.setBackgroundColor(backgroundColor);
         button4.setTextColor(foregroundColor);
         button4.setSelectedColor(selectionColor);

         FontIcon icon5 = new FontIcon();
         icon5.setIconCode(AntDesignIconsFilled.APPLE);
         icon5.setIconSize(32);
         icon5.setFill(foregroundColor);

         button5 = new NButton("Click");
         button5.setPrefSize(200, 80);
         button5.setFont(Font.font(24));
         button5.setButtonShape(ButtonShape.PILL);
         button5.setGraphics(icon5);
         button5.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
         button5.setBackgroundColor(backgroundColor);
         button5.setTextColor(foregroundColor);
         button5.setSelectedColor(selectionColor);

         FontIcon icon6 = new FontIcon();
         icon6.setIconCode(AntDesignIconsFilled.APPLE);
         icon6.setIconSize(32);
         icon6.setFill(foregroundColor);

         button6 = new NButton("Click");
         button6.setPrefSize(200, 80);
         button6.setFont(Font.font(24));
         button6.setButtonShape(ButtonShape.PILL);
         button6.setGraphics(icon6);
         button6.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
         button6.setBackgroundColor(backgroundColor);
         button6.setTextColor(foregroundColor);
         button6.setSelectable(true);
         button6.setSelectedColor(selectionColor);
         button6.selectedProperty().addListener((o, ov, nv) -> icon6.setFill(nv ? selectionColor : Color.WHITE));

         textField1 = new NTextField("TextField");
         textField1.setPrefSize(200, 80);
         textField1.setFont(Font.font(24));
         textField1.setBackgroundColor(backgroundColor);
         textField1.setTextColor(foregroundColor);
         textField1.setSelectedColor(selectionColor);

         ToggleGroup toggleGroup = new ToggleGroup();

         radioButton1 = new NRadioButton("RadioButton");
         radioButton1.setPrefSize(200, 24);
         radioButton1.setFont(Font.font(24));
         radioButton1.setBackgroundColor(backgroundColor);
         radioButton1.setTextColor(foregroundColor);
         radioButton1.setSelectedColor(selectionColor);
         radioButton1.setToggleGroup(toggleGroup);

         radioButton2 = new NRadioButton("RadioButton");
         radioButton2.setPrefSize(200, 24);
         radioButton2.setFont(Font.font(24));
         radioButton2.setBackgroundColor(backgroundColor);
         radioButton2.setTextColor(foregroundColor);
         radioButton2.setSelectedColor(selectionColor);
         radioButton2.setToggleGroup(toggleGroup);

         checkBox1 = new NCheckBox("Checkbox");
         checkBox1.setPrefSize(200, 24);
         checkBox1.setFont(Font.font(24));
         checkBox1.setBackgroundColor(backgroundColor);
         checkBox1.setTextColor(foregroundColor);
         checkBox1.setSelectedColor(selectionColor);

         checkBox2 = new NCheckBox("Checkbox");
         checkBox2.setPrefSize(200, 24);
         checkBox2.setFont(Font.font(24));
         checkBox2.setBackgroundColor(backgroundColor);
         checkBox2.setTextColor(foregroundColor);
         checkBox2.setSelectedColor(selectionColor);
     }

     @Override public void start(Stage stage) {
         VBox pane = new VBox(10, new HBox(10, button1, button2, button3, button4, button5, button6),
                              new HBox(10, textField1, new VBox(10, radioButton1, radioButton2), new VBox(10, checkBox1, checkBox2)));
         pane.setPadding(new Insets(20));
         pane.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));

         Scene scene = new Scene(pane);

         stage.setTitle("Neumorphic Demo");
         stage.setScene(scene);
         stage.centerOnScreen();
         stage.show();
     }

     @Override public void stop() {
         System.exit(0);
     }

     public static void main(String[] args) {
         launch(args);
     }
 }
