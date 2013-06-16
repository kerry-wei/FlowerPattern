import javax.swing.JColorChooser;
import javax.swing.JFrame;

public class ColorPickerPanel extends JColorChooser {
    private static final long serialVersionUID = 1L;

    JFrame frame;

    ColorPickerPanel() {
        frame = new JFrame("Color Picker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocation(700, 100);
        frame.setContentPane(this);
        frame.setVisible(true);
    }

}
