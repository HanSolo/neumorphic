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

 import eu.hansolo.fx.neumorphic.tools.ButtonShape;
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

 import java.util.List;


 /**
  * User: hansolo
  * Date: 30.12.20
  * Time: 06:56
  */
 public class Demo extends Application {
     private static final double             CONTROL_WIDTH          = 120;
     private static final double             CONTROL_HEIGHT         = 36;
     private static final double             CONTROL_FONT_SIZE      = 14;
     private static final Font               CONTROL_FONT           = Font.font(CONTROL_FONT_SIZE);
     private static final Color              BRIGHT_BACKGROUND      = Color.web("#e5e8eb");
     private static final Color              BRIGHT_FOREGROUND      = Color.web("#2e2f33");
     private static final Color              BRIGHT_SELECTION_COLOR = Color.web("#CC00C1");
     private static final Color              DARK_BACKGROUND        = Color.web("#2e2f33");
     private static final Color              DARK_FOREGROUND        = Color.web("#e5e8eb");
     private static final Color              DARK_SELECTION_COLOR   = Color.web("#00FFF1");
     private static final Color              BACKGROUND_COLOR       = DARK_BACKGROUND;
     private static final Color              FOREGROUND_COLOR       = DARK_FOREGROUND;
     private static final Color              SELECTION_COLOR        = DARK_SELECTION_COLOR;
     private              NButton            button1;
     private              NToggleButton      button2;
     private              NButton            button3;
     private              NToggleButton      button4;
     private              NButton            button5;
     private              NToggleButton      button6;
     private              NTextField         textField1;
     private              NTextField         textField2;
     private              NRadioButton       radioButton1;
     private              NRadioButton       radioButton2;
     private              NCheckBox          checkBox1;
     private              NCheckBox          checkBox2;
     private              NChoiceBox<String> choiceBox1;
     private              NSwitch            switch1;


     @Override public void init() {
         button1 = new NButton("Click");
         button1.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         button1.setFont(CONTROL_FONT);
         button1.setButtonShape(ButtonShape.PILL);
         button1.setBackgroundColor(BACKGROUND_COLOR);
         button1.setTextColor(FOREGROUND_COLOR);
         button1.armedProperty().addListener((o, ov, nv) -> { if (nv) { System.out.println("Armed"); } });
         button1.setOnAction(e -> System.out.println("Fired"));

         button2 = new NToggleButton("Selectable");
         button2.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         button2.setFont(CONTROL_FONT);
         button2.setBackgroundColor(BACKGROUND_COLOR);
         button2.setTextColor(FOREGROUND_COLOR);
         button2.setSelectedColor(SELECTION_COLOR);

         button3 = new NButton("1");
         button3.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         button3.setFont(CONTROL_FONT);
         button3.setButtonShape(ButtonShape.CIRCULAR);
         button3.setBackgroundColor(BACKGROUND_COLOR);
         button3.setTextColor(FOREGROUND_COLOR);

         button4 = new NToggleButton("1");
         button4.setPrefSize(CONTROL_HEIGHT, CONTROL_HEIGHT);
         button4.setFont(CONTROL_FONT);
         button4.setButtonShape(ButtonShape.CIRCULAR);
         button4.setBackgroundColor(BACKGROUND_COLOR);
         button4.setTextColor(FOREGROUND_COLOR);
         button4.setSelectedColor(SELECTION_COLOR);

         FontIcon icon5 = new FontIcon();
         icon5.setIconCode(AntDesignIconsFilled.APPLE);
         icon5.setIconSize((int) CONTROL_HEIGHT / 2);
         icon5.setFill(FOREGROUND_COLOR);

         button5 = new NButton("Click");
         button5.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         button5.setFont(CONTROL_FONT);
         button5.setButtonShape(ButtonShape.PILL);
         button5.setGraphics(icon5);
         button5.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
         button5.setBackgroundColor(BACKGROUND_COLOR);
         button5.setTextColor(FOREGROUND_COLOR);

         FontIcon icon6 = new FontIcon();
         icon6.setIconCode(AntDesignIconsFilled.APPLE);
         icon6.setIconSize((int) CONTROL_HEIGHT / 2);
         icon6.setFill(FOREGROUND_COLOR);

         button6 = new NToggleButton("Click");
         button6.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         button6.setFont(CONTROL_FONT);
         button6.setButtonShape(ButtonShape.PILL);
         button6.setGraphics(icon6);
         button6.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
         button6.setBackgroundColor(BACKGROUND_COLOR);
         button6.setTextColor(FOREGROUND_COLOR);
         button6.setSelectedColor(SELECTION_COLOR);
         button6.selectedProperty().addListener((o, ov, nv) -> icon6.setFill(nv ? SELECTION_COLOR : Color.WHITE));

         textField1 = new NTextField("TextField");
         textField1.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         textField1.setFont(CONTROL_FONT);
         textField1.setBackgroundColor(BACKGROUND_COLOR);
         textField1.setTextColor(FOREGROUND_COLOR);
         textField1.setSelectedColor(SELECTION_COLOR);

         textField2 = new NTextField("TextField");
         textField2.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         textField2.setFont(CONTROL_FONT);
         textField2.setBackgroundColor(BACKGROUND_COLOR);
         textField2.setTextColor(FOREGROUND_COLOR);
         textField2.setSelectedColor(SELECTION_COLOR);

         ToggleGroup toggleGroup = new ToggleGroup();

         radioButton1 = new NRadioButton("RadioButton");
         radioButton1.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         radioButton1.setFont(CONTROL_FONT);
         radioButton1.setBackgroundColor(BACKGROUND_COLOR);
         radioButton1.setTextColor(FOREGROUND_COLOR);
         radioButton1.setSelectedColor(SELECTION_COLOR);
         radioButton1.setToggleGroup(toggleGroup);

         radioButton2 = new NRadioButton("RadioButton");
         radioButton2.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         radioButton2.setFont(CONTROL_FONT);
         radioButton2.setBackgroundColor(BACKGROUND_COLOR);
         radioButton2.setTextColor(FOREGROUND_COLOR);
         radioButton2.setSelectedColor(SELECTION_COLOR);
         radioButton2.setToggleGroup(toggleGroup);

         checkBox1 = new NCheckBox("Checkbox");
         checkBox1.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         checkBox1.setFont(CONTROL_FONT);
         checkBox1.setBackgroundColor(BACKGROUND_COLOR);
         checkBox1.setTextColor(FOREGROUND_COLOR);
         checkBox1.setSelectedColor(SELECTION_COLOR);

         checkBox2 = new NCheckBox("Checkbox");
         checkBox2.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         checkBox2.setFont(CONTROL_FONT);
         checkBox2.setBackgroundColor(BACKGROUND_COLOR);
         checkBox2.setTextColor(FOREGROUND_COLOR);
         checkBox2.setSelectedColor(SELECTION_COLOR);

         List<String> items = List.of("Foo", "Bar", "abc", "def", "ghi");
         choiceBox1 = new NChoiceBox<>(items);
         choiceBox1.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         choiceBox1.setBackgroundColor(BACKGROUND_COLOR);
         choiceBox1.setTextColor(FOREGROUND_COLOR);
         choiceBox1.setSelectedColor(SELECTION_COLOR);

         switch1 = new NSwitch();
         switch1.setPrefSize(CONTROL_WIDTH, CONTROL_HEIGHT);
         switch1.setBackgroundColor(BACKGROUND_COLOR);
         switch1.setTextColor(FOREGROUND_COLOR);
         switch1.setSelectedColor(SELECTION_COLOR);
     }

     @Override public void start(Stage stage) {
         VBox pane = new VBox(10, new HBox(10, button1, button2, button3, button4, button5, button6),
                              new HBox(10, new VBox(10, textField1, textField2), new VBox(10, radioButton1, radioButton2), new VBox(10, checkBox1, checkBox2), new VBox(10, choiceBox1, switch1)));
         pane.setPadding(new Insets(20));
         pane.setBackground(new Background(new BackgroundFill(BACKGROUND_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

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
