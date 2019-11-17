import java.time.*;
import java.time.format.*;

import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.geo.*;
import de.fhpotsdam.unfolding.utils.*;
import de.fhpotsdam.unfolding.marker.*;

import de.fhpotsdam.unfolding.providers.Microsoft;

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


void setup()
{
    size(1024, 768, P2D);
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

void draw()
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

void mouseMoved() 
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