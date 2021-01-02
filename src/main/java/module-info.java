module eu.hansolo.fx.neumorphic {
    // Java
    requires java.base;

    // Java-FX
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;

    // 3rd Party
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.antdesignicons;

    exports eu.hansolo.fx.neumorphic;
    exports eu.hansolo.fx.neumorphic.tools;
}