import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FlowerPatternMain implements ChangeListener {
    FlowerPatternPanel canvas;
    ColorPickerPanel colorPicker;

    FlowerPatternMain() {
        canvas = FlowerPatternPanel.getInstance();
        colorPicker = new ColorPickerPanel();
        colorPicker.getSelectionModel().addChangeListener(this);
    }

    public static void main(String[] args) {
        // create the window         
        @SuppressWarnings("unused")
        FlowerPatternMain main = new FlowerPatternMain();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Color newColor = colorPicker.getColor();
        System.out.println("color changed: " + newColor.toString());
        canvas.updateColor(newColor);
    }

}
