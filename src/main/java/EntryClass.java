/*
 * This class puts it all together
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class EntryClass {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(e -> createAndShowTray());
    }

    private static void createAndShowTray() {
        // Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported!");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(createImage("tree.png", "tray icon"));
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu components
        MenuItem exitItem = new MenuItem("Exit");

        // Add components to popup menu
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }

        // Action Listers
        trayIcon.addActionListener(e -> getAndDisplayChristmasTree());

        exitItem.addActionListener(e -> {
                tray.remove(trayIcon);
                System.exit(0);
        });
    }

    private static void getAndDisplayChristmasTree() {
        String key = "AIzaSyCCuPp0ZLhOxKjXD9zjhSLjBHZX6VtyFF8";
        String cx = "015419240212191738490:poddwq4zyuc";

        ApiEngine engine = new ApiEngine(key, cx);

        try {
            BufferedImage img = engine.getRandomChristmasTreeImage();
            PictureFrame frame = new PictureFrame(img);
            frame.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    private static Image createImage(String path, String description) {
        URL imageURL = EntryClass.class.getClassLoader().getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else
            return (new ImageIcon(imageURL, description)).getImage();
    }
}
