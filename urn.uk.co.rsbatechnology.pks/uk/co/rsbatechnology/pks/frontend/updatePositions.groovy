import org.netkernel.layer0.nkf.INKFRequest
import org.netkernel.layer0.representation.impl.*;
import org.netkernel.layer0.representation.IHDSNode
import org.netkernel.layer0.representation.IHDSNodeList

/**
 * Physical endpoint to calculate and persist position changes for a FX source transaction
 */

// Obtain source FX transaction representation
IHDSNode sourceTransaction = context.source("arg:sourceTransaction")
 
IHDSNode builderEndpoints = context.source("active:discoverPositionBuilderEndpoints")

HDSBuilder b = new HDSBuilder()

for (IHDSNode endpoint in builderEndpoints.getNodes("//builderEndpoint")) {
	// Create request to each Position Builder
	endpointID = endpoint.getFirstValue("id")
	INKFRequest builderRequest = context.createRequestToEndpoint(endpointID)
	builderRequest.addArgumentByValue("operand", sourceTransaction)
	IHDSNode generatedDeltas = context.issueRequest(builderRequest)
	generatedDeltas.getValues("//positionDelta").each {b.addNode("positionDelta", it)}
}

IHDSNode deltaList = b.getRoot()

def asyncHandlers = []

for (deltaIdentifier in deltaList.getValues("/*")) {

	// Build position posting request
	// Issue each posting request asynchronously to improve posting performance across threads
	INKFRequest postingRequest = context.createRequest("active:positionPosting")
	postingRequest.addArgument("operand", deltaIdentifier)
	asyncHandlers << context.issueAsyncRequest(postingRequest)
}

// Wait for position postings to complete
asyncHandlers.each {it.join()}

context.createResponseFrom(deltaList)




 


