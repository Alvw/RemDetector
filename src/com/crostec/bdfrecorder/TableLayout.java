package com.crostec.bdfrecorder;

// Table layout manager, with the flexibility of GridBagLayout but the ease
// of use of HTML table declarations.
// See http://www.parallax.co.uk/~rolf/download/table.html

// Copyright (C) Rolf Howarth 1997, 1998 (rolf@parallax.co.uk)
// Permission to freely use, modify and distribute this code is given,
// provided this notice remains attached. This code is provided for
// educational use only and no warranty as to its suitability for any
// other purpose is made.

// Modification history
// 0.1  01 Nov 96  First version
// 1.0  17 Jan 97  Minor bug fix; added column weighting.
// 1.1  08 Apr 98  Don't use methods deprecated in JDK1.1
// 1.2  16 Apr 98  Make own copy of Dimension objects as they're not immutable

import java.awt.*;
import java.util.*;

// Private class to parse and store the options for a single table entry



/**
 * Table layout manager, with the flexibity of GridBagLayout but the ease of
 * use of HTML table declarations.
 *
 * <p>use like:   </br>
 *    new TableLayout(cols) </br>
 *    add(comp, new TableOption(..))  </br>
 *    ..
 * </p>
 */

public class TableLayout implements LayoutManager,LayoutManager2 {
    private Hashtable options = new Hashtable();
    private TableOption defaultOption;

    private int nrows=0, ncols=0;
    private int ncomponents=0;
    private Component[][] components=null;

    private int MinWidth=0, MinHeight=0, PrefWidth=0, PrefHeight=0;
    private int[] minWidth=null, minHeight=null, prefWidth=null, prefHeight=null;
    private int[] weight=null, columnWidth=null;
    private int hgap=0, vgap=0;

    /**
     * Construct a new table layout manager.
     * @param cols Number of columns, used when adding components to tell when to go to the next row
     * @param defaultAlignment Default defaultAlignment for cells if not specified at the time of adding the component
     * @param hgap Horizontal gap between cells and at edge (in pixels)
     * @param vgap Vertical gap between cells and at edge (in pixels)
     **/
    public TableLayout(int cols, String defaultAlignment, int hgap, int vgap) {
        this(cols, new TableOption(defaultAlignment),hgap,vgap);
    }

    public TableLayout(int cols, TableOption defaultAlignment, int hgap, int vgap) {
        this.ncols = cols;  // the number of columns is specified
        this.nrows = 0;   // the number of rows is calculated
        this.components = new Component[cols][];
        this.defaultOption=defaultAlignment;
        this.hgap = hgap;
        this.vgap = vgap;

    }

    public TableLayout(int cols, String alignment)
    {
        this(cols, alignment, 0, 0);
    }

    public TableLayout(int cols)
    {
        this(cols, "", 0, 0);
    }

    public void addLayoutComponent(String alignment, Component comp)
    {
        options.put(comp, new TableOption(alignment));
    }

    public void removeLayoutComponent(Component comp)
    {
        options.remove(comp);
    }

    // Iterate through the components, counting the number of rows taking into account
    // row and column spanning, then initialise the components[c][r] matrix so that
    // we can retrieve the component at a particular row,column position.

    private void loadComponents(Container parent)
    {
        ncomponents = parent.getComponentCount();

        // If we haven't allocated the right sized array for each column yet, do so now.
        // Note that the number of columns is fixed, but the number of rows is not know
        // and could in the worst case be up the number of components. Unfortunately this
        // means we need to allocate quite big arrays, but the alternative would require
        // complex multiple passes as we try to work out the effect of row spanning.
        if (components[0] == null || components[0].length < ncomponents)
        {
            for (int i=0; i<ncols; ++i)
                components[i] = new Component[ncomponents];
        }
        // Nullify the array
        for (int i=0; i<ncols; ++i)
        {
            for (int j=0; j<components[i].length; ++j)
                components[i][j] = null;
        }

        // fill the matrix with components, taking row/column spanning into account
        int row=0, col=0;
        for (int i=0; i<ncomponents; ++i)
        {
            // get the next component and its options
            Component comp = parent.getComponent(i);
            TableOption option = (TableOption) options.get(comp);
            if (option==null) option = defaultOption;

            // handle options to force us to column 0 or to skip columns
            if (option.forceColumn >= 0)
            {
                if (col > option.forceColumn)
                    ++row;
                col = option.forceColumn;
            }
            col += option.skipColumns;
            if (col>=ncols) { ++row; col=0; }

            // skip over any cells that are already occupied
            while (components[col][row] != null)
            {
                ++col;
                if (col>=ncols) { ++row; col=0; }
            }

            // if using colspan, will we fit on this row?
            if (col+option.colSpan > ncols)
            {
                ++row;
                col = 0;
            }

            // for now, fill all the cells that are occupied by this component
            for (int c=0; c<option.colSpan; ++c)
                for (int r=0; r<option.rowSpan; ++r)
                    components[col+c][row+r] = comp;

            // advance to the next cell, ready for the next component
            col += option.colSpan;
            if (col>=ncols) { ++row; col=0; }
        }

        // now we know how many rows there are
        if (col == 0)
            nrows = row;
        else
            nrows = row+1;

        // now we've positioned our components we can thin out the cells so
        // we only remember the top left corner of each component
        for (row=0; row<nrows; ++row)
        {
            for (col=0; col<ncols; ++col)
            {
                Component comp = components[col][row];
                for (int r=row; r<nrows && components[col][r]==comp; ++r)
                {
                    for (int c=col; c<ncols && components[c][r]==comp; ++c)
                    {
                        if (r>row || c>col)
                            components[c][r] = null;
                    }
                }
            }
        }
    }

    private void measureComponents(Container parent)
    {
        // set basic metrics such as ncomponents & nrows, and load the components
        // into the components[][] array.
        loadComponents(parent);

        // allocate new arrays to store row and column preferred and min sizes, but
        // only if the old arrays aren't big enough
        if (minWidth==null || minWidth.length<ncols)
        {
            minWidth = new int[ncols];
            prefWidth = new int[ncols];
            columnWidth = new int[ncols];
            weight = new int[ncols];
        }
        if (minHeight==null || minHeight.length<nrows)
        {
            minHeight = new int[nrows];
            prefHeight = new int[nrows];
        }

        int i;
        for (i=0; i<ncols; ++i)
        {
            minWidth[i] = 0;
            prefWidth[i] = 0;
        }
        for (i=0; i<nrows; ++i)
        {
            minHeight[i] = 0;
            prefHeight[i] = 0;
        }

        // measure the minimum and preferred available of each row and column

        for (int row=0; row<nrows; ++row)
        {
            for (int col=0; col<ncols; ++col)
            {
                Component comp = components[col][row];
                if (comp != null && comp.isVisible())
                {
                    TableOption option = (TableOption) options.get(comp);
                    if (option==null) option = defaultOption;

                    Dimension minSize = new Dimension(comp.getMinimumSize());
                    Dimension prefSize = new Dimension(comp.getPreferredSize());

                    // enforce prefSize>=minSize
                    if (prefSize.width < minSize.width)
                        prefSize.width = minSize.width;
                    if (prefSize.height < minSize.height)
                        prefSize.height = minSize.height;

                    // divide available across all the rows or columns being spanned
                    minSize.width /= option.colSpan;
                    minSize.height /= option.rowSpan;
                    prefSize.width = (prefSize.width - hgap*(option.colSpan-1)) / option.colSpan;
                    prefSize.height = (prefSize.height - vgap*(option.rowSpan-1)) / option.rowSpan;

                    for (int c=0; c<option.colSpan; ++c)
                    {
                        if (minSize.width > minWidth[col+c])
                            minWidth[col+c] = minSize.width;
                        if (prefSize.width > prefWidth[col+c])
                            prefWidth[col+c] = prefSize.width;
                    }

                    for (int r=0; r<option.rowSpan; ++r)
                    {
                        if (minSize.height > minHeight[row+r])
                            minHeight[row+r] = minSize.height;
                        if (prefSize.height > prefHeight[row+r])
                            prefHeight[row+r] = prefSize.height;
                    }
                }
            }
        }

        // add rows and columns to give total min and preferred available of whole grid

        MinWidth = 0;
        MinHeight = 0;
        PrefWidth = hgap;
        PrefHeight = vgap;

        for (i=0; i<ncols; ++i)
        {
            if (isColVisible(i))
            {
                MinWidth += minWidth[i];
                PrefWidth += prefWidth[i] + hgap;
            }
        }
        for (i=0; i<nrows; ++i)
        {
            MinHeight += minHeight[i];
            PrefHeight += prefHeight[i] + vgap;
        }
    }

    public boolean isColVisible(int colNumber)
    {
        boolean  isVisible = false;
        for (int row=0; row<nrows; ++row)
        {
            Component comp = components[colNumber][row];
            if(comp != null && comp.isVisible()) {
                isVisible = true;
            }
        }
        return isVisible;
    }

    public Dimension minimumLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();
        measureComponents(parent);
        // System.out.println("Min Size: "+MinWidth+","+MinHeight);
        return new Dimension(insets.left + insets.right + MinWidth,
                insets.top + insets.bottom + MinHeight);
    }

    public Dimension preferredLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();
        measureComponents(parent);
        // System.out.println("Pref Size: "+PrefWidth+","+PrefHeight);
        // System.out.println("+ insets LR "+insets.left+"+"+insets.right+", TB "+insets.top+"+"+insets.bottom);
        return new Dimension(insets.left + insets.right + PrefWidth,
                insets.top + insets.bottom + PrefHeight);
    }

    public void layoutContainer(Container parent)
    {
        Insets insets = parent.getInsets();
        measureComponents(parent);
        int width = parent.getSize().width - (insets.left + insets.right);
        int height = parent.getSize().height - (insets.top + insets.bottom);
        // System.out.println("Resize "+width+","+height);

        // Decide whether to base our scaling on minimum or preferred sizes, or
        // a mixture of both, separately for width and height scaling.
        // This weighting also tells us how much of the hgap/vgap to use.

        double widthWeighting = 0.0;
        if (width >= PrefWidth || PrefWidth==MinWidth)
            widthWeighting = 1.0;
        else if (width <= MinWidth)
        {
            widthWeighting = 0.0;
            width = MinWidth;
        }
        else
            widthWeighting = (double)(width-MinWidth)/(double)(PrefWidth-MinWidth);

        double heightWeighting = 0.0;
        if (height >= PrefHeight || PrefHeight==MinHeight)
            heightWeighting = 1.0;
        else if (height <= MinHeight)
        {
            heightWeighting = 0.0;
            height = MinHeight;
        }
        else
            heightWeighting = (double)(height-MinHeight)/(double)(PrefHeight-MinHeight);

        // calculate scale factors to scale components to available of container, based
        // on weighted combination of minimum and preferred sizes

        double minWidthScale = (1.0 - widthWeighting) * width/MinWidth;
        //double prefWidthScale = widthWeighting * (width-hgap*(ncols+1))/(PrefWidth-hgap*(ncols+1));
        double minHeightScale = (1.0 - heightWeighting) * height/MinHeight;
        double prefHeightScale = heightWeighting * (height-vgap*(nrows+1))/(PrefHeight-vgap*(nrows+1));

        // only get the full amount of gap if we're working to preferred available
        int vGap = (int) (vgap * heightWeighting);
        int hGap = (int) (hgap * widthWeighting);

        int y = insets.top + vGap;

        for (int c=0; c<ncols; ++c)
            weight[c] = prefWidth[c];

        for (int r=0; r<nrows; ++r)
        {
            int x = insets.left + hGap;
            int rowHeight = (int)(minHeight[r]*minHeightScale + prefHeight[r]*prefHeightScale);

            // Column padding can vary from row to row, so we need several
            // passes through the columns for each row:

            // First, work out the weighting that deterimines how we distribute column padding
            for (int c=0; c<ncols; ++c)
            {
                Component comp = components[c][r];
                if (comp != null && comp.isVisible())
                {
                    TableOption option = (TableOption) options.get(comp);
                    if (option==null) option = defaultOption;
                    if (option.weight >= 0)
                        weight[c] = option.weight;
                    else if (option.weight == -1)
                        weight[c] = prefWidth[c];
                }
            }
            int totalWeight = 0;
            for (int c=0; c<ncols; ++c)
                totalWeight += weight[c];
            int horizSurplus = width - hgap*(ncols+1) - PrefWidth;

            // Then work out column sizes, essentially preferred available + share of padding
            for (int c=0; c<ncols; ++c)
            {
                columnWidth[c] = (int) (minWidthScale * minWidth[c] + widthWeighting * prefWidth[c]);
                if (horizSurplus > 0 && totalWeight > 0)
                    columnWidth[c] += (int) (widthWeighting * horizSurplus * weight[c] / totalWeight);
            }

            // Only now do we know enough to position all the columns within this row...
            for (int c=0; c<ncols; ++c)
            {
                Component comp = components[c][r];
                if (comp != null && comp.isVisible())
                {
                    TableOption option = (TableOption) options.get(comp);
                    if (option==null) option = defaultOption;

                    // cell available may be bigger than row/column available due to spanning
                    int cellHeight = rowHeight;
                    int cellWidth = columnWidth[c];
                    for (int i=1; i<option.colSpan; ++i)
                        cellWidth += columnWidth[c+i];
                    for (int i=1; i<option.rowSpan; ++i)
                        cellHeight += (int)(minHeight[r+i]*minHeightScale + prefHeight[r+i]*prefHeightScale + vGap);

                    Dimension d = new Dimension(comp.getPreferredSize());

                    if (d.width > cellWidth || option.horizontal==TableOption.FILL)
                        d.width = cellWidth;
                    if (d.height > cellHeight || option.vertical==TableOption.FILL)
                        d.height = cellHeight;

                    int yoff = 0;
                    if (option.vertical == TableOption.BOTTOM)
                        yoff = cellHeight - d.height;
                    else if (option.vertical == TableOption.CENTRE)
                        yoff = (cellHeight - d.height) / 2;

                    int xoff = 0;
                    if (option.horizontal == TableOption.RIGHT)
                        xoff = cellWidth - d.width;
                    else if (option.horizontal == TableOption.CENTRE)
                        xoff = (cellWidth - d.width) / 2;

                    // System.out.println(" "+comp.getClass().getName()+" at ("+x+"+"+xoff+","+y+"+"+yoff+"), available "+d.width+","+d.height);
                    comp.setBounds(x+xoff,y+yoff,d.width,d.height);
                    x += columnWidth[c] + hGap;
                }
            }
            y += rowHeight + vGap;
        }
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        if(constraints instanceof TableOption){
            options.put(comp, constraints);
        }
        else if(constraints==null){
            options.put(comp,defaultOption);
        }
        else throw new IllegalArgumentException("not a valid constraints object="+constraints);

    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     * <p>
     * @return the value <code>0.5f</code> to indicate centered
     */
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     * <p>
     * @return the value <code>0.5f</code> to indicate centered
     */
    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) {
    }
    /**
     * Returns the maximum dimensions for this layout given the components
     * in the specified target container.
     * @param target the container which needs to be laid out
     * @see Container
     * @see #minimumLayoutSize(Container)
     * @see #preferredLayoutSize(Container)
     * @return the maximum dimensions for this layout
     */
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
}







