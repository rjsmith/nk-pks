
/**
 * SINKs a single position resource.
 * 
 * Position representation:
 * 
 * <position id="EFX-LDN-PRE_STP-ef79b770-b605-11e3-a5e2-0800200c9a66-NET-2014.03.27-EUR"
 * 		<amounts>
 * 			<amount id="amount1" symbol="EUR">1000</amount>
 * 			<amount id="base1" symbol="USD">1300</amount>
 * 		</amounts>
 * </position>
 * 
 * Uses PDS as back-end persistence engine
 */

import org.netkernel.layer0.representation.*
import org.netkernel.layer0.representation.impl.*;

POSITION_IDENTIFIER_DELIMITER = ":"

businessArea = context.getThisRequest().getArgumentValue("businessArea")
location = context.getThisRequest().getArgumentValue("location")
lifecycleType = context.getThisRequest().getArgumentValue("lifecycleType")
accountUUID = context.getThisRequest().getArgumentValue("accountUUID")
positionType = context.getThisRequest().getArgumentValue("positionType")
positionDate = context.getThisRequest().getArgumentValue("positionDate")
positionSymbol = context.getThisRequest().getArgumentValue("positionSymbol")

IHDSNode positionAmounts = context.sourcePrimary(IHDSNode.class)
IHDSNode firstAmountsNode = positionAmounts.getFirstNode("/amounts")
// TODO: Check if null, throw exception

positionIdentifier = businessArea+POSITION_IDENTIFIER_DELIMITER+location+POSITION_IDENTIFIER_DELIMITER+lifecycleType+POSITION_IDENTIFIER_DELIMITER+accountUUID+POSITION_IDENTIFIER_DELIMITER+positionType+POSITION_IDENTIFIER_DELIMITER+positionDate+POSITION_IDENTIFIER_DELIMITER+positionSymbol


// Create HDS structure
b = new HDSBuilder()
b.pushNode("position")
b.addNode("@id", positionIdentifier)
b.importNode(firstAmountsNode)

newPosition = b.getRoot()

// Use PDS to SINK position node
context.sink("pds:/pks/pos/"+positionIdentifier, newPosition)

// Return position representation as SINK request response
context.createResponseFrom(newPosition)
