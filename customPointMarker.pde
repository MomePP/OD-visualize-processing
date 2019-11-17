public class customPointMarker extends SimplePointMarker 
{
    protected String markerDetail;
    protected float size = 15;
    protected int space = 10;

    private float fontSize = 12;

    color _markerColor;

    public customPointMarker(Location location, color markerColor, String ldt_orig, String ldt_dest, long timeUsed, boolean isOrigin) 
    {
        super(location);
        _markerColor = markerColor;

        if (isOrigin)
        {
            markerDetail = "Origin\n";
        }
        else
        {
            markerDetail = "Destination\n";
        }
        markerDetail += "Start: " + ldt_orig + "\nEnd:  " + ldt_dest + "\nTotal: " + timeUsed + " minutes";
    }
    
    public void draw(PGraphics pg, float x, float y)
    {
        color m;
        if (isSelected()) 
        {
            m = color(_markerColor, 230);
        }
        else
        {
            m = color(_markerColor, 120);
        }

        pg.pushStyle();
        pg.noStroke();
        pg.fill(m);
        pg.ellipse(x, y, 15, 15);
        pg.fill(m);
        pg.ellipse(x, y, 10, 10);

        if (isSelected()) 
        {
            pg.translate(0, 0);
            pg.fill(color(m, 250));
            pg.stroke(m);
            pg.rect(x + strokeWeight / 2, y - fontSize + strokeWeight / 2 - space, pg.textWidth(markerDetail) + space * 1.5f, (fontSize + space) * 3);
            pg.fill(255, 255, 255);
            pg.text(markerDetail, Math.round(x + space * 0.75f + strokeWeight / 2), 
            Math.round(y + strokeWeight / 2 - space * 0.75f));
        }

        pg.popStyle();
    }
}
