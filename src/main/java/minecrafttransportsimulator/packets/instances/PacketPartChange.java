package minecrafttransportsimulator.packets.instances;

import io.netty.buffer.ByteBuf;
import minecrafttransportsimulator.baseclasses.Point3D;
import minecrafttransportsimulator.entities.components.AEntityF_Multipart;
import minecrafttransportsimulator.entities.instances.APart;
import minecrafttransportsimulator.items.components.AItemPart;
import minecrafttransportsimulator.jsondefs.JSONPartDefinition;
import minecrafttransportsimulator.mcinterface.AWrapperWorld;
import minecrafttransportsimulator.mcinterface.IWrapperNBT;
import minecrafttransportsimulator.mcinterface.InterfaceManager;
import minecrafttransportsimulator.packets.components.APacketEntity;
import minecrafttransportsimulator.systems.PackParserSystem;

/**Packet used to add/remove parts from an entity.  This packet only appears on clients after the
 * server has added or removed a part from the entity.
 * 
 * @author don_bruce
 */
public class PacketPartChange extends APacketEntity<AEntityF_Multipart<?>>{
	private final Point3D partOffset;
	private final AItemPart partItem;
	private final IWrapperNBT partData;
	private Point3D parentPartOffset;
	
	public PacketPartChange(AEntityF_Multipart<?> entity, Point3D partOffset){
		super(entity);
		this.partOffset = partOffset;
		this.partItem = null;
		this.partData = null;
		this.parentPartOffset = null;
	}
	
	public PacketPartChange(AEntityF_Multipart<?> entity, APart partAdded){
		super(entity);
		this.partOffset = partAdded.placementOffset;
		this.partItem = partAdded.getItem();
		this.partData = InterfaceManager.coreInterface.getNewNBTWrapper();
		partAdded.save(partData);
		this.parentPartOffset = partAdded.parentPart != null ? partAdded.parentPart.placementOffset : null;
	}
	
	public PacketPartChange(ByteBuf buf){
		super(buf);
		this.partOffset = readPoint3dFromBuffer(buf);
		if(buf.readBoolean()){
			this.partItem = PackParserSystem.getItem(readStringFromBuffer(buf), readStringFromBuffer(buf), readStringFromBuffer(buf));
			this.partData = readDataFromBuffer(buf);
			if(buf.readBoolean()){
				this.parentPartOffset = readPoint3dFromBuffer(buf);
			}else{
				this.parentPartOffset = null;
			}
		}else{
			this.partItem = null;
			this.partData = null;
			this.parentPartOffset = null;
		}
	}
	
	@Override
	public void writeToBuffer(ByteBuf buf){
		super.writeToBuffer(buf);
		writePoint3dToBuffer(partOffset, buf);
		if(partItem != null){
			buf.writeBoolean(true);
			writeStringToBuffer(partItem.definition.packID, buf);
			writeStringToBuffer(partItem.definition.systemName, buf);
			writeStringToBuffer(partItem.subName, buf);
			writeDataToBuffer(partData, buf);
			if(parentPartOffset != null){
				buf.writeBoolean(true);
				writePoint3dToBuffer(parentPartOffset, buf);
			}else{
				buf.writeBoolean(false);
			}
		}else{
			buf.writeBoolean(false);
		}
	}
	
	@Override
	public boolean handle(AWrapperWorld world, AEntityF_Multipart<?> entity){
		if(partItem == null){
			APart part = entity.getPartAtLocation(partOffset);
			if(part != null){
				entity.removePart(part, null);
			}
		}else{
			JSONPartDefinition packVehicleDef = entity.getPackDefForLocation(partOffset);
			entity.addPart(partItem.createPart(entity, null, packVehicleDef, partData, entity.getPartAtLocation(parentPartOffset)), false);
		}
		return true;
	}
}
