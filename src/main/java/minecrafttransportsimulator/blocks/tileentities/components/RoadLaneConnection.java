package minecrafttransportsimulator.blocks.tileentities.components;

import minecrafttransportsimulator.baseclasses.Point3i;
import minecrafttransportsimulator.mcinterface.WrapperNBT;

/**Helper class for containing connection data.
 * Contains location of connected road, as well
 * the lane connected to, and if we are connected
 * to the start of that lane or not.
 *
 * @author don_bruce
 */
public class RoadLaneConnection{
	public final Point3i tileLocation;
	public final int laneNumber;
	public final int curveNumber;
	public final boolean connectedToStart;
	
	public RoadLaneConnection(Point3i tileLocation, int laneNumber, int curveNumber, boolean connectedToStart){
		this.tileLocation = tileLocation;
		this.laneNumber = laneNumber;
		this.curveNumber = curveNumber;
		this.connectedToStart = connectedToStart;
	}
	
	public RoadLaneConnection(WrapperNBT data){
		this.tileLocation = data.getPoint3i("tileLocation");
		this.laneNumber = data.getInteger("laneNumber");
		this.curveNumber = data.getInteger("curveNumber");
		this.connectedToStart = data.getBoolean("connectedToStart");
	}
	
	public void save(WrapperNBT data){
		data.setPoint3i("tileLocation",tileLocation);
		data.setInteger("laneNumber", laneNumber);
		data.setInteger("curveNumber", curveNumber);
		data.setBoolean("connectedToStart", connectedToStart);
	}
}