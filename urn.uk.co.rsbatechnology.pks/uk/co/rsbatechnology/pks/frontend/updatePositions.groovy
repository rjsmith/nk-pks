import org.netkernel.layer0.nkf.INKFRequest
import org.netkernel.layer0.representation.IHDSNode
import org.netkernel.layer0.representation.IHDSNodeList

/**
 * Physical endpoint to calculate and persist position changes for a FX source transaction
 */

// Obtain source FX transaction representation
IHDSNode sourceTransaction = context.source("arg:sourceTransaction")
 
// Create request to Open Settlement Position Builder
INKFRequest builderRequest = context.createRequest("active:settlementPositionBuilder")
builderRequest.addArgumentByValue("operand", sourceTransaction)
IHDSNode generatedDeltas = context.issueRequest(builderRequest)

// Post these positions to the position cache
String[] deltaList = generatedDeltas.getValues("//positionDelta")

for (deltaIdentifier in deltaList)
{
	// Build position posting request
	// Issue each posting request asynchronously to improve posting performance across threads
	INKFRequest postingRequest = context.createRequest("active:positionPosting")
	postingRequest.addArgument("operand", deltaIdentifier)
	context.issueAsyncRequest(postingRequest)
}

context.createResponseFrom(generatedDeltas)



 


