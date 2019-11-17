import java.util.List;
import java.util.ArrayList;

public class customLinesMarker extends SimpleLinesMarker 
{
    color _markerColor;
    float _markerSize;

    long _timeUsed;
    long _timeElapsed;

    int count = 0;
    int update = 0;

    List<Float> lineVertexs = new ArrayList<Float>();

    public customLinesMarker(Location start, Location end, color markerColor, float markerSize, long timeUsed) 
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
