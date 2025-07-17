package org.example.gui.pdf_viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PdfDrawerGui extends JPanel implements MouseListener, MouseMotionListener {

    private boolean isCreatingRect = false;

    private int rectLeftTopX = 0;
    private int rectLeftTopY = 0;
    private int rectRightDownX = 0;
    private int rectRightDownY = 0;

    private Consumer<Rectangle2D.Float> onSelect;

    public PdfDrawerGui(Consumer<Rectangle2D.Float> onSelect){

        addMouseListener(this);
        addMouseMotionListener(this);

        this.onSelect = onSelect;
    }

    public PdfDrawerGui(){

        this(null);
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

            handleShowSelectedArea();

            if(onSelect != null){

                Rectangle2D.Float rect = generateRect();

                onSelect.accept(rect);
            }

            rectLeftTopX = 0;
            rectLeftTopY = 0;
            rectRightDownX = 0;
            rectRightDownY = 0;
        }
    }

    private Rectangle2D.Float generateRect(){

        float minX = rectLeftTopX;
        float minY = rectLeftTopY;
        float maxX = rectRightDownX;
        float maxY = rectRightDownY;
        float width = maxX - minX;
        float height = maxY - minY;

        return new Rectangle2D.Float(minX, minY, width, height);
    }

    private void handleShowSelectedArea(){

        String leftTopCords = "A = (" + rectLeftTopX + ", " + rectLeftTopY + ")";
        String rightDownCords = "B = (" + rectRightDownX + ", " + rectRightDownY + ")";

        JOptionPane.showMessageDialog(
            null,
            leftTopCords + ", " + rightDownCords,
            "Powiadomienie",
            JOptionPane.INFORMATION_MESSAGE
        );
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
