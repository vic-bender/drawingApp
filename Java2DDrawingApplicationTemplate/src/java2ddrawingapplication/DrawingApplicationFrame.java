/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author acv, mod. by Victor A. Bender
 */
public class DrawingApplicationFrame extends JFrame
{

    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    JPanel top = new JPanel();
    
    JPanel line1 = new JPanel();
    JPanel line2 = new JPanel();
    
    // create the widgets for the firstLine Panel.
    JLabel shapeWidget = new JLabel("Shape: ");
    
    String ssText[] = {"Line", "Oval" , "Rectangle"};
    JComboBox shapeSelector = new JComboBox(ssText);
    
    JButton primaryColor = new JButton("1st Color");
    JButton secondaryColor = new JButton("2nd Color");
    JButton undo = new JButton("Undo");
    JButton clear = new JButton("Clear");
    
    //create the widgets for the secondLine Panel.
    JLabel options = new JLabel("Options: ");
    
    JCheckBox filled = new JCheckBox("Filled");
    JCheckBox gradient = new JCheckBox("Use Gradient");
    JCheckBox dashed = new JCheckBox("Dashed");

    JLabel lwLabel = new JLabel("Line Width:");
    JSpinner lwField = new JSpinner(new SpinnerNumberModel(10, 3, 100, 1));

    JLabel dlLabel = new JLabel("Dash Length:");
    JSpinner dlField = new JSpinner(new SpinnerNumberModel(15, 3, 100, 1));
    
    // Variables for drawPanel.
    DrawPanel drawPanel = new DrawPanel();
    
    ArrayList<MyShapes> allShapes = new ArrayList<MyShapes>();
    
    int lineWidth = 10;
    double dashLength = 15;
    
    Color color1 = Color.BLACK;
    Color color2 = Color.BLACK;
    
    // add status label
    JLabel status = new JLabel();
    
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        this.setLayout(new BorderLayout());
        
        top.setLayout(new BorderLayout());
        
        // add widgets to panels
        // firstLine widgets
        line1.add(shapeWidget);
        
        line1.add(shapeSelector);
        
        line1.add(primaryColor);
        line1.add(secondaryColor);
        line1.add(undo);
        line1.add(clear);
        
        // secondLine widgets
        line2.add(options);
        
        line2.add(filled);
        line2.add(gradient);
        line2.add(dashed);
        
        line2.add(lwLabel);
        line2.add(lwField);
        
        line2.add(dlLabel);
        line2.add(dlField);
        
        // add top panel of two panels
        top.add(line1, BorderLayout.NORTH);
        top.add(line2, BorderLayout.SOUTH);
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        this.add(top, BorderLayout.NORTH);
        this.add(drawPanel, BorderLayout.CENTER);
        this.add(status, BorderLayout.SOUTH);
        
        //add listeners and event handlers
        
        primaryColor.addActionListener(listener -> {
            color1 = JColorChooser.showDialog(null, "Select Color 1", color1);
        });
        
        secondaryColor.addActionListener(listener -> {
            color2 = JColorChooser.showDialog(null, "Select Color 2", color2);   
        });
        
        undo.addActionListener(listener -> {
            if(!allShapes.isEmpty())
            {
                allShapes.remove(allShapes.size() - 1);
                drawPanel.repaint();
            }
        });
        
        clear.addActionListener(listener -> {
            allShapes.clear();
            drawPanel.repaint();
        });
    }

    // Create event handlers, if needed
    
    // event handlers not needed, this section left blank
    
    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {
        
        public Point startPoint;
        ArrayList<MyShapes> tempShapes = new ArrayList<MyShapes>();

        
        public DrawPanel()
        {
            addMouseListener(new MouseHandler());
            addMouseMotionListener(new MouseHandler());
        }
        
        private MyShapes buildShape(Point start, Point end) // builds the shape from existing MyShapes characteristics
        {
            BasicStroke strk = dashed.isSelected() 
                ? new BasicStroke(Integer.parseInt(lwField.getValue().toString()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{Float.parseFloat(dlField.getValue().toString())}, 0) 
                : new BasicStroke(Integer.parseInt(lwField.getValue().toString()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); 
            Paint paint = gradient.isSelected() 
                ? new GradientPaint(0, 0, color1, 50, 50, color2, true) 
                : new GradientPaint(0, 0, color1, 50, 50, color1, true);

            switch(shapeSelector.getSelectedItem().toString())
            {
                case "Line":
                    return new MyLine(start, end, paint, strk);
                case "Oval":
                    return new MyOval(start, end, paint, strk, filled.isSelected());
                case "Rectangle":
                    return new MyRectangle(start, end, paint, strk, filled.isSelected());
                default:
                    return null;
            }
        }
        
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
            for (MyShapes shape : allShapes) {
                shape.draw(g2d);
            }
            
            for (MyShapes shape : tempShapes) {
                shape.draw(g2d);
            }
            tempShapes.clear();
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {

            public void mousePressed(MouseEvent event)
            {
                startPoint = event.getPoint();
            }

            public void mouseReleased(MouseEvent event)
            {
                MyShapes currentShape = buildShape(startPoint, event.getPoint());
                if (currentShape != null)
                {
                    allShapes.add(currentShape);
                    drawPanel.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                status.setText("(" + event.getX() + "," + event.getY() + ")");
                MyShapes currShape = buildShape(startPoint, event.getPoint());
                if (currShape != null)
                {
                    tempShapes.add(currShape);
                    drawPanel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                status.setText("(" + event.getX() + "," + event.getY() + ")");
            }
        }

    }
}
