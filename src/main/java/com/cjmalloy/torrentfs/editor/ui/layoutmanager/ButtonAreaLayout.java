package com.cjmalloy.torrentfs.editor.ui.layoutmanager;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.SwingConstants;

/**
 * <code>ButtonAreaLayout</code> behaves in a similar manner to
 * <code>FlowLayout</code>. It lays out all components from left to
 * right. If <code>syncAllWidths</code> is true, the widths of each
 * component will be set to the largest preferred size width.
 *
 * This class should be treated as a &quot;protected&quot; inner class.
 * Instantiate it only within subclasses of {@code BasicOptionPaneUI}.
 */
public class ButtonAreaLayout implements LayoutManager {
    protected boolean           syncAllWidths;
    protected int               padding;
    /** If true, children are lumped together in parent. */
    protected boolean           centersChildren;
    private int orientation;
    private boolean reverseButtons;
    /**
     * Indicates whether or not centersChildren should be used vs
     * the orientation. This is done for backward compatibility
     * for subclassers.
     */
    private boolean useOrientation;

    public ButtonAreaLayout(boolean syncAllWidths, int padding) {
        this.syncAllWidths = syncAllWidths;
        this.padding = padding;
        centersChildren = true;
        useOrientation = false;
    }

    public ButtonAreaLayout(boolean syncAllSizes, int padding, int orientation,
                     boolean reverseButtons) {
        this(syncAllSizes, padding);
        useOrientation = true;
        this.orientation = orientation;
        this.reverseButtons = reverseButtons;
    }

    public void setSyncAllWidths(boolean newValue) {
        syncAllWidths = newValue;
    }

    public boolean getSyncAllWidths() {
        return syncAllWidths;
    }

    public void setPadding(int newPadding) {
        this.padding = newPadding;
    }

    public int getPadding() {
        return padding;
    }

    public void setCentersChildren(boolean newValue) {
        centersChildren = newValue;
        useOrientation = false;
    }

    public boolean getCentersChildren() {
        return centersChildren;
    }

    private int getOrientation(Container container) {
        if (!useOrientation) {
            return SwingConstants.CENTER;
        }
        if (container.getComponentOrientation().isLeftToRight()) {
            return orientation;
        }
        switch (orientation) {
        case SwingConstants.LEFT:
            return SwingConstants.RIGHT;
        case SwingConstants.RIGHT:
            return SwingConstants.LEFT;
        case SwingConstants.CENTER:
            return SwingConstants.CENTER;
        }
        return SwingConstants.LEFT;
    }

    public void addLayoutComponent(String string, Component comp) {
    }

    public void layoutContainer(Container container) {
        Component[]      children = container.getComponents();

        if(children != null && children.length > 0) {
            int               numChildren = children.length;
            Insets            insets = container.getInsets();
            int maxWidth = 0;
            int maxHeight = 0;
            int totalButtonWidth = 0;
            int x = 0;
            int xOffset = 0;
            boolean ltr = container.getComponentOrientation().
                                    isLeftToRight();
            boolean reverse = (ltr) ? reverseButtons : !reverseButtons;

            for(int counter = 0; counter < numChildren; counter++) {
                Dimension pref = children[counter].getPreferredSize();
                maxWidth = Math.max(maxWidth, pref.width);
                maxHeight = Math.max(maxHeight, pref.height);
                totalButtonWidth += pref.width;
            }
            if (getSyncAllWidths()) {
                totalButtonWidth = maxWidth * numChildren;
            }
            totalButtonWidth += (numChildren - 1) * padding;

            switch (getOrientation(container)) {
            case SwingConstants.LEFT:
                x = insets.left;
                break;
            case SwingConstants.RIGHT:
                x = container.getWidth() - insets.right - totalButtonWidth;
                break;
            case SwingConstants.CENTER:
                if (getCentersChildren() || numChildren < 2) {
                    x = (container.getWidth() - totalButtonWidth) / 2;
                }
                else {
                    x = insets.left;
                    if (getSyncAllWidths()) {
                        xOffset = (container.getWidth() - insets.left -
                                   insets.right - totalButtonWidth) /
                            (numChildren - 1) + maxWidth;
                    }
                    else {
                        xOffset = (container.getWidth() - insets.left -
                                   insets.right - totalButtonWidth) /
                                  (numChildren - 1);
                    }
                }
                break;
            }

            for (int counter = 0; counter < numChildren; counter++) {
                int index = (reverse) ? numChildren - counter - 1 :
                            counter;
                Dimension pref = children[index].getPreferredSize();

                if (getSyncAllWidths()) {
                    children[index].setBounds(x, insets.top,
                                              maxWidth, maxHeight);
                }
                else {
                    children[index].setBounds(x, insets.top, pref.width,
                                              pref.height);
                }
                if (xOffset != 0) {
                    x += xOffset;
                }
                else {
                    x += children[index].getWidth() + padding;
                }
            }
        }
    }

    public Dimension minimumLayoutSize(Container c) {
        if(c != null) {
            Component[]       children = c.getComponents();

            if(children != null && children.length > 0) {
                Dimension     aSize;
                int           numChildren = children.length;
                int           height = 0;
                Insets        cInsets = c.getInsets();
                int           extraHeight = cInsets.top + cInsets.bottom;
                int           extraWidth = cInsets.left + cInsets.right;

                if (syncAllWidths) {
                    int              maxWidth = 0;

                    for(int counter = 0; counter < numChildren; counter++){
                        aSize = children[counter].getPreferredSize();
                        height = Math.max(height, aSize.height);
                        maxWidth = Math.max(maxWidth, aSize.width);
                    }
                    return new Dimension(extraWidth + (maxWidth * numChildren) +
                                         (numChildren - 1) * padding,
                                         extraHeight + height);
                }
                else {
                    int        totalWidth = 0;

                    for(int counter = 0; counter < numChildren; counter++){
                        aSize = children[counter].getPreferredSize();
                        height = Math.max(height, aSize.height);
                        totalWidth += aSize.width;
                    }
                    totalWidth += ((numChildren - 1) * padding);
                    return new Dimension(extraWidth + totalWidth, extraHeight + height);
                }
            }
        }
        return new Dimension(0, 0);
    }

    public Dimension preferredLayoutSize(Container c) {
        return minimumLayoutSize(c);
    }

    public void removeLayoutComponent(Component c) { }
}