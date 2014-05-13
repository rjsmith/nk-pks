
/**
 * SINKs a single position delta resource.
 * 
 * Position delta representation:
 * 
 <positionDelta>
	<@positionID>EFX:LDN:PRE_STP:ef79b770-b605-11e3-a5e2-0800200c9a66:OPEN:2014.03.27:EUR</@id>
	<deltaSequence>1399554731345</deltaSequence>
	<deltaAmounts>
		<deltaAmount>
			<action>DEBIT</action>
			<type>SETTLEMENT</type>
		 	<symbol>EUR</symbol>
		 	<value>10000</value>
		</deltaAmount>
		<deltaAmount>
			<action>DEBIT</action>
			<type>BASE</type>
		 	<symbol>USD</symbol>
		 	<value>13000</value>
		</deltaAmount>
	</deltaAmounts>
	<sourceTransaction>
		<id>9f8a49c9-3111-417a-9024-80e7c9c69595</id>
		<version>1</version>
	</sourceTransaction>
</positionDelta>
 * 
 * Uses PDS as back-end persistence engine
 */

import org.netkernel.layer0.representation.*
import org.netkernel.layer0.representation.impl.*;
import org.netkernel.layer0.nkf.*;

POSITION_IDENTIFIER_DELIMITER = ":"
POSITION_DELTA_PDS_PREFIX = "pds:/pks/delta/"

// Parse common request arguments
businessArea = context.source("arg:businessArea")
location = context.source("arg:location")
lifecycleType = context.source("arg:lifecycleType")
accountUUID = context.source("arg:accountUUID")
positionType = context.source("arg:positionType")
positionDate = context.source("arg:positionDate")
positionSymbol = context.source("arg:positionSymbol")
deltaSequence = context.source("arg:deltaSequence")

positionDeltaIdentifier = businessArea+
POSITION_IDENTIFIER_DELIMITER+location+
POSITION_IDENTIFIER_DELIMITER+lifecycleType+
POSITION_IDENTIFIER_DELIMITER+accountUUID+
POSITION_IDENTIFIER_DELIMITER+positionType+
POSITION_IDENTIFIER_DELIMITER+positionDate+
POSITION_IDENTIFIER_DELIMITER+positionSymbol+
POSITION_IDENTIFIER_DELIMITER+deltaSequence

switch (context.getThisRequest().getVerb())
{
	case INKFRequestReadOnly.VERB_SINK:
		IHDSNode sourceTransaction = context.source("arg:sourceTransaction", IHDSNode.class)
		IHDSNode firstSourceTransactionNode = sourceTransaction.getFirstNode("/sourceTransaction")
		// TODO: Check if null, throw exception
		
		IHDSNode deltaAmounts = context.sourcePrimary(IHDSNode.class)
		IHDSNode firstAmountsNode = deltaAmounts.getFirstNode("/deltaAmounts")
		// TODO: Check if null, throw exception
		
		// Create HDS structure
		b = new HDSBuilder()
		b.pushNode("positionDelta")
		b.addNode("@id", positionDeltaIdentifier)
		b.addNode("deltaSequence", deltaSequence)
		b.importNode(firstAmountsNode)
		b.importNode(firstSourceTransactionNode)
		
		// Use PDS to SINK position delta node
		context.sink(POSITION_DELTA_PDS_PREFIX+positionDeltaIdentifier, b.getRoot())
		break
	case INKFRequestReadOnly.VERB_SOURCE:
		request = context.createRequest(POSITION_DELTA_PDS_PREFIX+positionDeltaIdentifier)
		response = context.issueRequestForResponse(request)
		context.createResponseFrom(response)
		break
	case INKFRequestReadOnly.VERB_DELETE:
		request = context.createRequest(POSITION_DELTA_PDS_PREFIX+positionDeltaIdentifier)
		request.setVerb(INKFRequestReadOnly.VERB_DELETE)
		response = context.issueRequestForResponse(request)
		context.createResponseFrom(response)
		break
	case INKFRequestReadOnly.VERB_EXISTS:
		request = context.createRequest(POSITION_DELTA_PDS_PREFIX+positionDeltaIdentifier)
		request.setVerb(INKFRequestReadOnly.VERB_EXISTS)
		response = context.issueRequestForResponse(request)
		context.createResponseFrom(response)
		break
}

