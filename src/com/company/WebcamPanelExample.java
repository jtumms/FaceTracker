package com.company;

/**
 * Created by john.tumminelli on 10/15/16.
 */
import javax.swing.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.applet.*;

import com.github.sarxos.webcam.Webcam;


public class WebcamPanelExample extends Applet {


    public static void main(String[] args) {

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);

        panel.setSize(400, 400);
        panel.setFPSDisplayed(false);
        panel.setDisplayDebugInfo(false);
        panel.setImageSizeDisplayed(false);
        panel.setMirrored(true);

        JButton button = new JButton("SAVE IMAGE");

        //add action listener with anonymous class

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buttonPressed(webcam);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });



        JFrame window = new JFrame("Take Picture");
        window.getContentPane().add(panel, BorderLayout.NORTH);
        window.getContentPane().add(button, BorderLayout.SOUTH);
        window.setResizable(false);
        window.setSize(400, 400);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }
    public static void buttonPressed(Webcam webcam) throws IOException {
        BufferedImage image = webcam.getImage();
        File f = new File("test.jpg");
        ImageIO.write(image, "JPG", f);

    }
}