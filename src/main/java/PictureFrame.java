import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class PictureFrame extends JFrame {
    PictureFrame(BufferedImage img) {
        super("Christmas Tree"); // Sets Window title

        // Gets image dimensions
        int img_width = img.getWidth();
        int img_height = img.getHeight();

        // Gets Default Screen's Dimensions
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int src_width = gd.getDisplayMode().getWidth();
        int src_height = gd.getDisplayMode().getHeight();

        // Scales image according to screen size
        AffineTransform at = new AffineTransform();
        double scale_suggestion = suggest_scale(src_width - 120, src_height - 120, img_width, img_height);
        at.scale(scale_suggestion, scale_suggestion);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

        // Gets image new dimensions
        int new_width = (int) Math.round(img_width * scale_suggestion);
        int new_height = (int) Math.round(img_height * scale_suggestion);

        BufferedImage new_img = new BufferedImage(new_width, new_height, img.getType());
        new_img = scaleOp.filter(img, new_img); // applies the scale

        ImageIcon icon = new ImageIcon(new_img);

        setLayout(new FlowLayout());
        setSize(new_width + 30, new_height + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        add(lbl);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private double suggest_scale(int src_w, int src_h, int img_w, int img_h) {
        double scale_amount = 1.0;

        if (img_w > src_w)
            scale_amount = src_w * 1.0f / img_w;
        if (img_h > src_h) {
            float sc = src_h * 1.0f / img_h;
            if (sc < scale_amount) scale_amount = sc;
        }
        return scale_amount;
    }
}
