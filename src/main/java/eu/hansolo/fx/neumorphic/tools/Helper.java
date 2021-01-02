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

package eu.hansolo.fx.neumorphic.tools;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Helper {

    public static void enableNode(final Node node, final boolean enable) {
        node.setVisible(enable);
        node.setManaged(enable);
    }

    public static final int clamp(final int min, final int max, final int value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static final long clamp(final long min, final long max, final long value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    public static final double clamp(final double min, final double max, final double value) {
        if (Double.compare(value, min) < 0) return min;
        if (Double.compare(value, max) > 0) return max;
        return value;
    }

    public static final Color getColorWithOpacity(final Color color, final double opacity) {
        return Color.color(color.getRed(), color.getGreen(), color.getBlue(), clamp(0.0, 1.0, opacity));
    }

    public static final Color getColorAt(final List<Stop> stops, final double position) {
        Map<Double, Stop> stopMap = new TreeMap<>();
        for (Stop stop : stops) { stopMap.put(stop.getOffset(), stop); }

        if (stopMap.isEmpty()) return Color.BLACK;

        double minFraction = Collections.min(stopMap.keySet());
        double maxFraction = Collections.max(stopMap.keySet());

        if (Double.compare(minFraction, 0d) > 0) { stopMap.put(0.0, new Stop(0.0, stopMap.get(minFraction).getColor())); }
        if (Double.compare(maxFraction, 1d) < 0) { stopMap.put(1.0, new Stop(1.0, stopMap.get(maxFraction).getColor())); }

        final double pos = clamp(0d, 1d, position);
        final Color color;
        if (stopMap.size() == 1) {
            final Map<Double, Color> ONE_ENTRY = (Map<Double, Color>) stopMap.entrySet().iterator().next();
            color = stopMap.get(ONE_ENTRY.keySet().iterator().next()).getColor();
        } else {
            Stop lowerBound = stopMap.get(0.0);
            Stop upperBound = stopMap.get(1.0);
            for (Double fraction : stopMap.keySet()) {
                if (Double.compare(fraction,pos) < 0) {
                    lowerBound = stopMap.get(fraction);
                }
                if (Double.compare(fraction, pos) > 0) {
                    upperBound = stopMap.get(fraction);
                    break;
                }
            }
            color = interpolateColor(lowerBound, upperBound, pos);
        }
        return color;
    }
    public static final Color interpolateColor(final Stop lowerBound, final Stop upperBound, final double position) {
        final double pos  = (position - lowerBound.getOffset()) / (upperBound.getOffset() - lowerBound.getOffset());

        final double deltaRed     = (upperBound.getColor().getRed()     - lowerBound.getColor().getRed())     * pos;
        final double deltaGreen   = (upperBound.getColor().getGreen()   - lowerBound.getColor().getGreen())   * pos;
        final double deltaBlue    = (upperBound.getColor().getBlue()    - lowerBound.getColor().getBlue())    * pos;
        final double deltaOpacity = (upperBound.getColor().getOpacity() - lowerBound.getColor().getOpacity()) * pos;

        double red     = clamp(0, 1, (lowerBound.getColor().getRed()     + deltaRed));
        double green   = clamp(0, 1, (lowerBound.getColor().getGreen()   + deltaGreen));
        double blue    = clamp(0, 1, (lowerBound.getColor().getBlue()    + deltaBlue));
        double opacity = clamp(0, 1, (lowerBound.getColor().getOpacity() + deltaOpacity));

        return Color.color(red, green, blue, opacity);
    }

    public static final double[] toHSL(final Color color) {
        return rgbToHSL(color.getRed(), color.getGreen(), color.getBlue());
    }
    public static final double[] rgbToHSL(final double red, final double green, final double blue) {
        //	Minimum and Maximum RGB values are used in the HSL calculations
        double min = Math.min(red, Math.min(green, blue));
        double max = Math.max(red, Math.max(green, blue));

        //  Calculate the Hue
        double hue = 0;

        if (max == min) {
            hue = 0;
        } else if (max == red) {
            hue = ((60 * (green - blue) / (max - min)) + 360) % 360;
        } else if (max == green) {
            hue = (60 * (blue - red) / (max - min)) + 120;
        } else if (max == blue) {
            hue = (60 * (red - green) / (max - min)) + 240;
        }

        //  Calculate the Luminance
        double luminance = (max + min) / 2;

        //  Calculate the Saturation
        double saturation = 0;
        if (Double.compare(max, min)  == 0) {
            saturation = 0;
        } else if (luminance <= .5) {
            saturation = (max - min) / (max + min);
        } else {
            saturation = (max - min) / (2 - max - min);
        }

        return new double[] { hue, saturation, luminance};
    }

    public static final Color hslToRGB(double hue, double saturation, double luminance) {
        return hslToRGB(hue, saturation, luminance, 1);
    }
    public static final Color hslToRGB(double hue, double saturation, double luminance, double opacity) {
        saturation = clamp(0, 1, saturation);
        luminance  = clamp(0, 1, luminance);
        opacity    = clamp(0, 1, opacity);

        hue = hue % 360.0;
        hue /= 360;

        double q = luminance < 0.5 ? luminance * (1 + saturation) : (luminance + saturation) - (saturation * luminance);
        double p = 2 * luminance - q;

        double r = clamp(0, 1, hueToRGB(p, q, hue + (1.0 / 3.0)));
        double g = clamp(0, 1, hueToRGB(p, q, hue));
        double b = clamp(0, 1, hueToRGB(p, q, hue - (1.0 / 3.0)));

        return Color.color(r, g, b, opacity);
    }
    private static final double hueToRGB(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (6 * t < 1) { return p + ((q - p) * 6 * t); }
        if (2 * t < 1) { return q; }
        if (3 * t < 2) { return p + ((q - p) * 6 * ((2.0/3.0) - t)); }
        return p;
    }

    public static final Color derive(final Color color, final double brightnessFactor) {
        double hue        = color.getHue();
        double brightness = color.getBrightness();
        double saturation = color.getSaturation();
        if (brightness == 0 && brightnessFactor > 0) { brightness = 0.05; }
        brightness = clamp(0, 1, brightness * brightnessFactor);
        return Color.hsb(hue, saturation, brightness);
    }

    public static final boolean isBright(final Color color) { return Double.compare(colorToYUV(color)[0], 0.5) >= 0.0; }
    public static final boolean isDark(final Color color) { return colorToYUV(color)[0] < 0.5; }

    public static final double[] colorToYUV(final Color color) {
        final double WEIGHT_FACTOR_RED   = 0.299;
        final double WEIGHT_FACTOR_GREEN = 0.587;
        final double WEIGHT_FACTOR_BLUE  = 0.144;
        final double U_MAX               = 0.436;
        final double V_MAX               = 0.615;
        double y = clamp(0, 1, WEIGHT_FACTOR_RED * color.getRed() + WEIGHT_FACTOR_GREEN * color.getGreen() + WEIGHT_FACTOR_BLUE * color.getBlue());
        double u = clamp(-U_MAX, U_MAX, U_MAX * ((color.getBlue() - y) / (1 - WEIGHT_FACTOR_BLUE)));
        double v = clamp(-V_MAX, V_MAX, V_MAX * ((color.getRed() - y) / (1 - WEIGHT_FACTOR_RED)));
        return new double[] { y, u, v };
    }

    public static final String colorToCss(final Color color) {
        return color.toString().replace("0x", "#");
    }
}
