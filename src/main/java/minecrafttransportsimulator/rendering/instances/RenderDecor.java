package minecrafttransportsimulator.rendering.instances;

import java.util.Set;

import minecrafttransportsimulator.baseclasses.BoundingBox;
import minecrafttransportsimulator.baseclasses.ColorRGB;
import minecrafttransportsimulator.baseclasses.Point3D;
import minecrafttransportsimulator.baseclasses.TransformationMatrix;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntityDecor;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntitySignalController;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntitySignalController.SignalDirection;
import minecrafttransportsimulator.blocks.tileentities.instances.TileEntitySignalController.SignalGroup;
import minecrafttransportsimulator.mcinterface.InterfaceManager;
import minecrafttransportsimulator.rendering.components.ARenderEntityDefinable;

public class RenderDecor extends ARenderEntityDefinable<TileEntityDecor>{
	private static final TransformationMatrix holoboxTransform = new TransformationMatrix();
	
	@Override
	protected void renderHolographicBoxes(TileEntityDecor decor, TransformationMatrix transform){
		//Render lane holo-boxes if we are a signal controller that's being edited.
		if(decor instanceof TileEntitySignalController){
			TileEntitySignalController controller = (TileEntitySignalController) decor;
			if(controller.unsavedClientChangesPreset || InterfaceManager.renderingInterface.shouldRenderBoundingBoxes()){
				for(Set<SignalGroup> signalGroupSet : controller.signalGroups.values()){
		        	for(SignalGroup signalGroup : signalGroupSet){ 
						if(signalGroup.signalLineWidth != 0 && controller.intersectionProperties.get(signalGroup.axis).isActive){
							//Get relative center coord.
							//First start with center signal line, which is distance from center of intersection to the edge of the stop line..
							Point3D boxRelativeCenter = signalGroup.signalLineCenter.copy();
							//Add 8 units to center on the box which is 16 units long.
							boxRelativeCenter.add(0, 0, 8);
							//Rotate box based on signal orientation to proper signal.
							boxRelativeCenter.rotate(signalGroup.axis.rotation);
							
							//Add delta from controller to intersection center.
							boxRelativeCenter.add(controller.intersectionCenterPoint).subtract(controller.position);
							boxRelativeCenter.y += 1;
							
							//Create bounding box and transform for it..
							BoundingBox box = new BoundingBox(boxRelativeCenter, signalGroup.signalLineWidth/2D, 1, 8);
							holoboxTransform.set(transform).applyTranslation(boxRelativeCenter).applyRotation(signalGroup.axis.rotation);
							box.renderHolographic(holoboxTransform, null, signalGroup.direction.equals(SignalDirection.LEFT) ? ColorRGB.BLUE : (signalGroup.direction.equals(SignalDirection.RIGHT) ? ColorRGB.YELLOW : ColorRGB.GREEN));
						}
		        	}
				}
			}
		}
	}
}
