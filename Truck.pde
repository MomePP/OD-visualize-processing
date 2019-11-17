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
    
    color markerColor;
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

    void setAlreadyStartedFlag()
    {
        alreadyStarted = true;
    }

    void setAlreadyStoppedFlag()
    {
        alreadyStopped = true;
    }
};

