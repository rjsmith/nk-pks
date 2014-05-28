import org.netkernel.layer0.representation.IHDSNode
import org.netkernel.layer0.representation.impl.HDSBuilder

/**
 * Builds list of endpoints which can generate position deltas
 * Returns HDS list of builder endpoint IDs and Space URIs
 */

IHDSNode rep=getUserMetaData()

HDSBuilder b = new HDSBuilder()
b.pushNode("builderEndpoints")
// Find first endpoint with <pks-builder> meta data element
builderMetaDataNodes = rep.getNodes("//pks-builder")
for (builderMetaDataNode in builderMetaDataNodes) {
	builderEndpointID = builderMetaDataNode.getParent().getParent().getFirstValue("id") // Get endpoint ID
	builderSpaceURI = builderMetaDataNode.getParent().getParent().getParent().getParent().getFirstValue("id") // Get space ID
	b.pushNode("builderEndpoint")
	b.addNode("id",builderEndpointID)
	b.addNode("space", builderSpaceURI)
	b.popNode()
}
context.createResponseFrom(b.getRoot())


def getUserMetaData() {
	req=context.createRequest("active:userMetaAggregator")
	req.addArgument("originonly", "true")
	req.setRepresentationClass(IHDSNode.class)
	rep=context.issueRequest(req)
	return rep
}




