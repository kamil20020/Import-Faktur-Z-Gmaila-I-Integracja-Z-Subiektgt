package org.example.gui.manage_pdf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class PdfDrawerGui extends JPanel implements MouseListener, MouseMotionListener {

    private boolean isCreatingRect = false;

    private int rectLeftTopX = 0;
    private int rectLeftTopY = 0;
    private int rectRightDownX = 0;
    private int rectRightDownY = 0;

    public PdfDrawerGui(){

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if(rectLeftTopY == rectRightDownY){

            return;
        }

        g.setColor(Color.RED);

        g.drawRect(rectLeftTopX, rectLeftTopY, rectRightDownX - rectLeftTopX, rectRightDownY - rectLeftTopY);
    }

    @Override
    public void mouseClicked(MouseEvent e) {


    }

    @Override
    public void mousePressed(MouseEvent e) {

        if(!isCreatingRect){

            rectLeftTopX = e.getX();
            rectLeftTopY = e.getY();
        }

        isCreatingRect = !isCreatingRect;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if(!isCreatingRect){

            String leftTopCords = "A = (" + rectLeftTopX + ", " + rectLeftTopY + ")";
            String rightDownCords = "B = (" + rectRightDownX + ", " + rectRightDownY + ")";

            JOptionPane.showMessageDialog(
                null,
                leftTopCords + ", " + rightDownCords,
                "Powiadomienie",
                JOptionPane.INFORMATION_MESSAGE
            );

            rectLeftTopX = 0;
            rectLeftTopY = 0;
            rectRightDownX = 0;
            rectRightDownY = 0;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {


    }

    @Override
    public void mouseExited(MouseEvent e) {


    }

    @Override
    public void mouseDragged(MouseEvent e) {


    }

    @Override
    public void mouseMoved(MouseEvent e) {

        if(isCreatingRect){

            rectRightDownX = e.getX();
            rectRightDownY = e.getY();

            repaint();
        }
    }

}
