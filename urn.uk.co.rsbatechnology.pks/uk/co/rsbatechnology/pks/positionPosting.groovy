/**
 * Posts a Position Delta to the Position Cache
 *
 */

import org.netkernel.layer0.representation.*
import org.netkernel.layer0.representation.impl.*;
import org.netkernel.layer0.nkf.*;

IHDSNode position
IHDSNode positionDelta
IHDSNodeList currentPositionAmountsNodes
HDSBuilder b

// Obtain representation of position delta
try {
	positionDelta = context.source("arg:operand", IHDSNode.class)
} catch (Exception e)
{
	nkfe = new NKFException("pks:positionposting:missingdelta",
		"Cannot obtain position delta representatation", e)
	throw nkfe
}

println "positionDelta:"+positionDelta

IHDSNode positionIdentifier = positionDelta.getFirstNode("//@id")

println "positionIdentifier"+positionIdentifier
def positionIdentifierURI = "pos:"+positionIdentifier.value

IHDSNodeList deltaAmountNodes = positionDelta.getNodes("//deltaAmounts/deltaAmount")
println "deltaAmountNodes:"+deltaAmountNodes

// Obtain existing representation of affected position, if any
if (context.exists(positionIdentifierURI)) 
{
	println "Position exists"
	position = context.source(positionIdentifierURI, IHDSNode.class)
	currentPositionAmountsNodes = position.getNodes("//amounts/amount")
		
} else
	println "Position non-existent"

// Initialise denominated currency amounts structure for updated position 
b = new HDSBuilder()
b.pushNode("amounts")

// Perform posting action
for (deltaAmountNode in deltaAmountNodes)
{
	println "Processing deltaAmountNode:"+deltaAmountNode
	deltaAction = deltaAmountNode.getFirstValue("//action")
	deltaAmountType = deltaAmountNode.getFirstValue("//type")
	deltaAmountSymbol = deltaAmountNode.getFirstValue("//symbol")
	deltaAmountValue = deltaAmountNode.getFirstValue("//value")
	
	def deltaAmount
	try {
		deltaAmount = deltaAmountValue.toInteger()
	} catch (Exception e)
	{
		nkfe = new NKFException("pks:positionposting:invalidpositiondeltaamountvalue",
			"Position Delta amount value is not a valid integer:"+deltaAmountValue, e)
		throw nkfe
	}
	
	// Get value of amount type from current position, if any
	def currentPositionAmount = 0
	if (currentPositionAmountsNodes != null)
	{
		for (positionAmountNode in currentPositionAmountsNodes) {
			positionAmountType = positionAmountNode.getFirstValue("//type")
			if (positionAmountType.equalsIgnoreCase(deltaAmountType))
				{
					positionAmountValue = positionAmountNode.getFirstValue("//value")
					try {
						currentPositionAmount = positionAmountValue?.toInteger()
					} catch (NumberFormatException e)
					{
						nkfe = new NKFException("pks:positionposting:invalidpositionamount",
							"Position amount is not a valid integer:"+positionAmountValue, e)
						throw nkfe					
					}
				}
		}
		
	}
	
	// Decide which position action to take
	def updatedPositionAmount
	switch (deltaAction)
	{
		case "CREDIT":
			updatedPositionAmount = currentPositionAmount + deltaAmount.abs()
			println "CREDIT updatedPositionAmount"+updatedPositionAmount
			break
		case "DEBIT":
			updatedPositionAmount = currentPositionAmount - deltaAmount.abs()
			println "DEBIT updatedPositionAmount"+updatedPositionAmount
			break
		case "SYNC":
			updatedPositionAmount = deltaAmount
			println "SYNC updatedPositionAmount"+updatedPositionAmount
			break
		default:
			nkfe = new NKFException("pks:positionposting:invaliddeltaaction",
				"Position Delta action is not valid:"+deltaAction)
			throw nkfe		
	}
	
	if (updatedPositionAmount != null) 
	{
		// Add amount node to updated position amounts HDS structure
		b.pushNode("amount")
		b.addNode("type", deltaAmountType)
		b.addNode("symbol", deltaAmountSymbol)
		b.addNode("value", updatedPositionAmount)
		b.popNode()
	}
	
}

IHDSNode updatedPositionAmountsNode = b.getRoot()
println "updatedPositionAmountsNode"+updatedPositionAmountsNode

// Use active:position service to persist updated position
sinkPositionRequest = context.createRequest(positionIdentifierURI)
sinkPositionRequest.setVerb(INKFRequestReadOnly.VERB_SINK)
sinkPositionRequest.addPrimaryArgument(updatedPositionAmountsNode)
try {
	sinkPositionResponse = context.issueRequestForResponse(sinkPositionRequest)
	context.createResponseFrom(sinkPositionResponse)
} catch (Exception e)
{
	nkfe = new NKFException("pks:positionposting:positionNotUpdated",
		"Re-calculated position could not be persisted", e)
	throw nkfe
}


