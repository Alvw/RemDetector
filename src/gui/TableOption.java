package gui;

import java.util.StringTokenizer;

public class TableOption{
    public static final int CENTRE=1, FILL=2, LEFT=3, RIGHT=4, TOP=5, BOTTOM=6;
    int horizontal = CENTRE;
    int vertical = CENTRE;
    int rowSpan=1, colSpan=1, skipColumns=0, forceColumn=-1, weight=-2;

    /**
     *
     * @param horizontal one of CENTRE,FILL,LEFT,RIGHT,TOP,BOTTOM
     * @param vertical
     */
    public TableOption(int horizontal, int vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public TableOption(int horizontal, int vertical, int rowSpan, int colSpan) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.rowSpan = rowSpan;
        this.colSpan = colSpan;
    }

    public TableOption(int horizontal, int vertical, int rowSpan, int colSpan, int skipColumns, int forceColumn, int weight) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.rowSpan = rowSpan;
        this.colSpan = colSpan;
        this.skipColumns = skipColumns;
        this.forceColumn = forceColumn;
        this.weight = weight;
    }

    TableOption(String alignment) {
        StringTokenizer tk = new StringTokenizer(alignment, ",");
        while (tk.hasMoreTokens())
        {
            String token = tk.nextToken();
            boolean ok = false;
            int delim = token.indexOf("=");
            if (token.equals("NW") || token.equals("W") || token.equals("SW"))
            { horizontal = LEFT; ok=true; }
            if (token.equals("NE") || token.equals("E") || token.equals("SE"))
            { horizontal = RIGHT; ok=true; }
            if (token.equals("N") || token.equals("C") || token.equals("F"))
            { horizontal = CENTRE; ok=true; }
            if (token.equals("F") || token.equals("FH"))
            { horizontal = FILL; ok=true; }
            if (token.equals("N") || token.equals("NW") || token.equals("NE"))
            { vertical = TOP; ok=true; }
            if (token.equals("S") || token.equals("SW") || token.equals("SE"))
            { vertical = BOTTOM; ok=true; }
            if (token.equals("W") || token.equals("C") || token.equals("E"))
            { vertical = CENTRE; ok=true; }
            if (token.equals("F") || token.equals("FV"))
            { vertical = FILL; ok=true; }
            if (delim>0)
            {
                int val = Integer.parseInt(token.substring(delim+1));
                token = token.substring(0,delim);
                if (token.equals("CS") && val>0)
                { colSpan = val; ok=true; }
                else if (token.equals("RS") && val>0)
                { rowSpan = val; ok=true; }
                else if (token.equals("SKIP") && val>0)
                { skipColumns = val; ok=true; }
                else if (token.equals("COL"))
                { forceColumn = val; ok=true; }
                else if (token.equals("WT"))
                { weight = val; ok=true; }
            }
            if (!ok) throw new IllegalArgumentException("TableOption "+token);
        }
    }
}


