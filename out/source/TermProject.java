import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.time.*; 
import java.time.format.*; 
import de.fhpotsdam.unfolding.*; 
import de.fhpotsdam.unfolding.geo.*; 
import de.fhpotsdam.unfolding.utils.*; 
import de.fhpotsdam.unfolding.marker.*; 
import de.fhpotsdam.unfolding.providers.Microsoft; 
import java.util.List; 
import java.util.ArrayList; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class TermProject extends PApplet {











// //* calgary maps
// //* 50.778155,-115.039215,51.322030,-113.670044

// //? http://bboxfinder.com/#48.013750,-121.000000,60.973010,-109.001400
// //* min-max lat-lng from raw data
// //* lat origin          48.02042    60.97301
// //* lng origin          -109.0008   -121
// //* lat destination     48.01375    60.97301
// //* lng destination     -109.0014   -121

UnfoldingMap map;
MarkerManager<Marker> markerManager;
Location currentMaps = new Location(52.5f, -113.5f);

Table truck_rawdata;
Truck[] truck_data;

DateTimeFormatter date_format = DateTimeFormatter.ofPattern("dd-MM-yy");
DateTimeFormatter ldt_string_format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

LocalDateTime time_units = LocalDateTime.of(LocalDate.parse("14-03-16", date_format), LocalTime.parse("00:00:00"));

float rotateZ = (float) 0;
float rotateVelocityZ = 0.003f;


public void setup()
{
    
    // size(800, 600, P3D);
    // frameRate(30);
    noStroke();

    //? maps init
    map = new UnfoldingMap(this, 0, 0, 1200, 900, new Microsoft.RoadProvider());
    map.zoomAndPanTo(currentMaps, 6);

    MapUtils.createDefaultEventDispatcher(this, map);
    markerManager = map.getDefaultMarkerManager();

    //? data init
    truck_rawdata = loadTable("truck1week.csv", "header");
    println(truck_rawdata.getRowCount() + " total rows in table");

    truck_data = new Truck[truck_rawdata.getRowCount()];

    int index = 0;
    for (TableRow row : truck_rawdata.rows()) 
    {
        //? get origin lat-long
        float lat_orig = row.getFloat("Latitude_orig");
        float lng_orig = row.getFloat("Longitude_orig");

        //? get destination lat-long
        float lat_dest = row.getFloat("Latitude_dest");
        float lng_dest = row.getFloat("Longitude_dest");

        //? get transportation duration
        LocalDateTime time_orig = LocalDateTime.of(LocalDate.parse(row.getString("date_orig"), date_format), LocalTime.parse(row.getString("time_orig")));
        LocalDateTime time_dest = LocalDateTime.of(LocalDate.parse(row.getString("date_dest"), date_format), LocalTime.parse(row.getString("time_dest")));
        
        Long trans_duration_minutes = Duration.between(time_orig, time_dest).toMinutes();
        
        truck_data[index++] = new Truck(lat_orig, lng_orig, lat_dest, lng_dest, time_orig, time_dest, trans_duration_minutes);
    }
    println("finish process the data");

    // Truck first_truck = new Truck(lat_orig, lng_orig, lat_dest, lng_dest, trans_duration_minutes);
    // println(truck_data[0].origin.Latitude);
    // println(truck_data[0].origin.Longtitude);
    // println(truck_data[0].destination.Latitude);
    // println(truck_data[0].destination.Longtitude);
    // println(truck_data[0].time_used);
}

public void draw()
{
    // if (frameCount % 1 == 0)
    // {
        background(100);

        //? rotate the maps
        pushMatrix();
        // translate(width / 2, height / 3, 100);
        // rotateX(0.8);
        // rotateZ(rotateZ);
        // translate(-map.getWidth() / 2, -map.getHeight() / 2);
        
        //? create marker and remove by time
        // for (int i = 0; i < 1; i++)
        for (int i = 0; i < truck_data.length; i++)
        {
            long start_elapsed = Duration.between(time_units, truck_data[i].ldt_orig).toMinutes();
            long end_elapsed = Duration.between(time_units, truck_data[i].ldt_dest).toMinutes();
            // println(end_elapsed);

            // if (start_elapsed < 0 && !truck_data[i].alreadyStarted)
            // {
            //     truck_data[i].toggleAlreadyStartedFlag();
            // }
            // else if (truck_data[i].alreadyStarted && end_elapsed > 0)
            // {
            //     truck_data[i].drawMarker(end_elapsed);
            // }

            if (start_elapsed < 0 && !truck_data[i].alreadyStarted)
            {
                markerManager.addMarker(truck_data[i].startMarker);
                markerManager.addMarker(truck_data[i].endMarker);
                markerManager.addMarker(truck_data[i].connectionMarker);
                truck_data[i].setAlreadyStartedFlag();
            }
            else if (truck_data[i].alreadyStarted && end_elapsed < 0 && !truck_data[i].alreadyStopped)
            {
                markerManager.removeMarker(truck_data[i].startMarker);
                markerManager.removeMarker(truck_data[i].endMarker);
                markerManager.removeMarker(truck_data[i].connectionMarker);
                truck_data[i].setAlreadyStoppedFlag();
            }
            else if (truck_data[i].alreadyStarted && !truck_data[i].alreadyStopped)
            {
                truck_data[i].connectionMarker.updateMarker(end_elapsed);
            }
        }

        //? update maps
        map.draw();

        popMatrix();

        //? update time units
        time_units = time_units.plusMinutes(1);
        // println(time_units);

        //? draw time on screen
        String currentTime = time_units.format(ldt_string_format);
        fill(170,150);
        rect(10, 5, textWidth(currentTime) + 20, 20);
        fill(255);
        text(currentTime, 20, 20);
    // }

    // rotateZ += rotateVelocityZ;
}

public void mouseMoved() 
{
    Marker hitMarker = markerManager.getFirstHitMarker(mouseX, mouseY);
    if (hitMarker != null) {
        // Select current marker 
        hitMarker.setSelected(true);
    } else {
        // Deselect all other markers
        for (Marker marker : markerManager.getMarkers()) {
            marker.setSelected(false);
        }
    }
}

// void mouseReleased()
// {
//     connectionMarker.setSelected();
// }
// void mouseMoved() {
//   // Deselect all marker
//   for (Marker marker : map.getMarkers()) {
//     marker.setSelected(false);
//   }

//   // Select hit marker
//   // Note: Use getHitMarkers(x, y) if you want to allow multiple selection.
//   Marker marker = map.getFirstHitMarker(mouseX, mouseY);
//   if (marker != null) {
//     marker.setSelected(true);
//   }
// }
class Truck {

    Location originLocation;  
    Location destinationLocation;

    // ScreenPosition originPosition;
    // ScreenPosition destinationPosition;
    // float currentPosX;
    // float currentPosY;
    customPointMarker startMarker;
    customPointMarker endMarker;
    customLinesMarker connectionMarker;
    
    LocalDateTime ldt_orig;
    LocalDateTime ldt_dest;
    long timeUsed;
    
    boolean alreadyStarted = false;
    boolean alreadyStopped = false;
    
    int markerColor;
    float markerSize;

    Truck (float lat_orig, float lng_orig, float lat_dest, float lng_dest, LocalDateTime time_orig, LocalDateTime time_dest, long time)
    {
        originLocation = new Location(lat_orig, lng_orig);
        destinationLocation = new Location(lat_dest, lng_dest);

        // originPosition = map.getScreenPosition(originLocation); 
        // destinationPosition = map.getScreenPosition(destinationLocation);

        ldt_orig = time_orig;
        ldt_dest = time_dest;
        timeUsed = time;

        markerColor = color(random(255),random(255), random(255), 120);
        // markerSize = 2;
        markerSize = map(timeUsed, 0, 10000, 2, 4);

        startMarker = new customPointMarker(originLocation, markerColor, time_orig.format(ldt_string_format), time_dest.format(ldt_string_format), timeUsed, true);
        endMarker = new customPointMarker(destinationLocation, markerColor, time_orig.format(ldt_string_format), time_dest.format(ldt_string_format), timeUsed, false);
        connectionMarker = new customLinesMarker(originLocation, destinationLocation, markerColor, markerSize, timeUsed);
    }

    // void updateMarker(long timeElapsed)
    // {
    // }

    // void drawMarker(long timeElapsed)
    // {
    //     //? map time elapsed with duration and boundaries X, Y
    //     float currentPosX = map(timeElapsed, timeUsed, 0, originPosition.x, destinationPosition.x);
    //     float currentPosY = map(timeElapsed, timeUsed, 0, originPosition.y, destinationPosition.y);

    //     pushStyle();
	// 	noFill();
    //     stroke(markerColor);
	// 	strokeWeight(markerSize);
	// 	smooth();

	// 	beginShape(LINES);
	// 	// MapPosition last = mapPositions.get(0);
	// 	// for (int i = 1; i < mapPositions.size(); ++i) {
	// 	// 	MapPosition mp = mapPositions.get(i);
	// 	// 	vertex(last.x, last.y);
	// 	// 	vertex(mp.x, mp.y);

	// 	// 	last = mp;
	// 	// }
    //     vertex(originPosition.x, originPosition.y);
    //     vertex(currentPosX, currentPosY);
	// 	endShape();
	// 	popStyle();
    // }

    public void setAlreadyStartedFlag()
    {
        alreadyStarted = true;
    }

    public void setAlreadyStoppedFlag()
    {
        alreadyStopped = true;
    }
};




public class customLinesMarker extends SimpleLinesMarker 
{
    int _markerColor;
    float _markerSize;

    long _timeUsed;
    long _timeElapsed;

    int count = 0;
    int update = 0;

    List<Float> lineVertexs = new ArrayList<Float>();

    public customLinesMarker(Location start, Location end, int markerColor, float markerSize, long timeUsed) 
    {
        addLocations(start, end);
        _markerColor = markerColor;
        _markerSize = markerSize;
        _timeUsed = timeUsed;
        // lineVertexs.add(0.0f);
    }

    public void updateMarker(long timeElapsed)
    {
        _timeElapsed = timeElapsed;

        // count++;
        // if (count % 20 == 0)
        // {
        //     float mapTimeToRadius = map(_timeElapsed, _timeUsed, 0, 0, PI);
        //     float currentPosZ = sin(mapTimeToRadius) * 300;
        //     lineVertexs.add(currentPosZ);
        // }
    }

    public void draw(PGraphics pg, List<MapPosition> mapPositions)
    {
        // pg.pushStyle();

        // // Here you should do your custom drawing
        // pg.strokeWeight(3);
        // pg.stroke(255, 0, 0);
        // pg.beginShape();
        // for (MapPosition mapPosition : mapPositions) {
        //     pg.vertex(mapPosition.x, mapPosition.y);
        // }
        // pg.endShape();

        // pg.popStyle();

        if (mapPositions.isEmpty() || isHidden() || _timeElapsed <= 0)
			return;

        pg.pushStyle();

		pg.noFill();
        if (isSelected()) 
        {
			pg.stroke(color(_markerColor), 200);
		}
        else
        {
			pg.stroke(_markerColor);
		}
		pg.strokeWeight(_markerSize);
		pg.smooth();

		pg.beginShape(PConstants.LINES);
		// pg.beginShape();

        //? map time elapsed with duration and boundaries X, Y
		MapPosition start = mapPositions.get(0);
		MapPosition end = mapPositions.get(1);

        float mapTimeToRadius = map(_timeElapsed, _timeUsed, 0, 0, PI);
        
        float currentPosZ = sin(mapTimeToRadius) * 300;
        float currentPosX = map(_timeElapsed, _timeUsed, 0, start.x, end.x);
        float currentPosY = map(_timeElapsed, _timeUsed, 0, start.y, end.y);

        // pg.curveVertex(start.x, start.y, 0);
        // for (float z : lineVertexs)
        // {
        //     pg.curveVertex(currentPosX, currentPosY, z);
        // }


        pg.vertex(start.x, start.y);
        pg.vertex(currentPosX, currentPosY);

		pg.endShape();
		
        pg.popStyle();
    }
}
public class customPointMarker extends SimplePointMarker 
{
    protected String markerDetail;
    protected float size = 15;
    protected int space = 10;

    private float fontSize = 12;

    int _markerColor;

    public customPointMarker(Location location, int markerColor, String ldt_orig, String ldt_dest, long timeUsed, boolean isOrigin) 
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
        int m;
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
  public void settings() {  size(1024, 768, P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "TermProject" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
