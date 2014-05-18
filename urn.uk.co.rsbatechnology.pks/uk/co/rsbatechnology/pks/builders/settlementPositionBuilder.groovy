/**
 * Open settlement position builder.
 * 
 * Creates and persists position deltas from a source transaction
 * Returns HDS structure with identity of created deltas
 */

import org.netkernel.layer0.representation.*
import org.netkernel.layer0.representation.impl.*;
import org.netkernel.layer0.nkf.*;

HDSBuilder b

POSITION_IDENTIFIER_DELIMITER = ":"

// Obtain source FX transaction representation
IHDSNode sourceTransaction = context.source("arg:sourceTransaction")
 
// Extract common attributes from transaction representation
businessArea = sourceTransaction.getFirstValue("//lifecycle/businessArea")
location = sourceTransaction.getFirstValue("//lifecycle/location")
transactionID = sourceTransaction.getFirstValue("//lifecycle/transactionID")
version = sourceTransaction.getFirstValue("//lifecycle/version")
lifecycleType = sourceTransaction.getFirstValue("//lifecycle/lifecycleType")
account = sourceTransaction.getFirstValue("//parties/counterparty")

//Build response with list of position delta identifiers
b = new HDSBuilder()
b.pushNode("positionDeltas")

if (sourceTransaction.getFirstNode("//fxspotoutright") != null) {
	valueDate = sourceTransaction.getFirstValue("//fxspotoutright/valueDate")
	// TODO: Ensure (or convert) this is in correct format: yyyy.mm.dd
	counterpartyBuysBase = sourceTransaction.getFirstValue("//fxspotoutright/counterpartyBuysBase")
	baseCurrencySymbol = sourceTransaction.getFirstValue("//baseCurrency/symbol")
	baseCurrencySettlementAmount = Float.parseFloat(sourceTransaction.getFirstValue("//baseCurrency/settlementAmount"))
	baseCurrencySettlementAmountUSD = Float.parseFloat(sourceTransaction.getFirstValue("//baseCurrency/settlementAmountUSD"))
	termCurrencySymbol = sourceTransaction.getFirstValue("//termCurrency/symbol")
	termCurrencySettlementAmount = Float.parseFloat(sourceTransaction.getFirstValue("//termCurrency/settlementAmount"))
	termCurrencySettlementAmountUSD = Float.parseFloat(sourceTransaction.getFirstValue("//termCurrency/settlementAmountUSD"))
	
	sourceTransactionNode = createSourceTransactionNode(transactionID, version)
	deltaSequence = System.currentTimeMillis()
	
	// Create position delta for base currency
	IHDSNode baseCurrencyDeltaAmountsNode = generateOpenDeltaAmountsForCurrency(counterpartyBuysBase, baseCurrencySymbol,baseCurrencySettlementAmount, baseCurrencySettlementAmountUSD )
	
	// Create position delta for term currency
	IHDSNode termCurrencyDeltaAmountsNode = generateOpenDeltaAmountsForCurrency(!counterpartyBuysBase, termCurrencySymbol,termCurrencySettlementAmount, termCurrencySettlementAmountUSD )

	// Persist position deltas
	sinkPositionDeltaRequest = context.createRequest("active:positionDelta")
	sinkPositionDeltaRequest.setVerb(INKFRequestReadOnly.VERB_SINK)
	sinkPositionDeltaRequest.addArgumentByValue("businessArea", businessArea)
	sinkPositionDeltaRequest.addArgumentByValue("location", location)
	sinkPositionDeltaRequest.addArgumentByValue("lifecycleType", lifecycleType)
	sinkPositionDeltaRequest.addArgumentByValue("accountUUID", account)
	sinkPositionDeltaRequest.addArgumentByValue("positionType", "OPEN")
	sinkPositionDeltaRequest.addArgumentByValue("positionDate", valueDate)
	sinkPositionDeltaRequest.addArgumentByValue("positionSymbol", baseCurrencySymbol)
	sinkPositionDeltaRequest.addArgumentByValue("deltaSequence", deltaSequence)
	sinkPositionDeltaRequest.addArgumentByValue("sourceTransaction", sourceTransactionNode)
	sinkPositionDeltaRequest.addPrimaryArgument(baseCurrencyDeltaAmountsNode)
	context.issueAsyncRequest(sinkPositionDeltaRequest)
	deltaIdentifier = "delta:"+businessArea+POSITION_IDENTIFIER_DELIMITER+location+POSITION_IDENTIFIER_DELIMITER+lifecycleType+POSITION_IDENTIFIER_DELIMITER+account+POSITION_IDENTIFIER_DELIMITER+"OPEN"+POSITION_IDENTIFIER_DELIMITER+valueDate+POSITION_IDENTIFIER_DELIMITER+baseCurrencySymbol+POSITION_IDENTIFIER_DELIMITER+deltaSequence
	b.addNode("positionDelta", deltaIdentifier)
	
	sinkPositionDeltaRequest = context.createRequest("active:positionDelta")
	sinkPositionDeltaRequest.setVerb(INKFRequestReadOnly.VERB_SINK)
	sinkPositionDeltaRequest.addArgumentByValue("businessArea", businessArea)
	sinkPositionDeltaRequest.addArgumentByValue("location", location)
	sinkPositionDeltaRequest.addArgumentByValue("lifecycleType", lifecycleType)
	sinkPositionDeltaRequest.addArgumentByValue("accountUUID", account)
	sinkPositionDeltaRequest.addArgumentByValue("positionType", "OPEN")
	sinkPositionDeltaRequest.addArgumentByValue("positionDate", valueDate)
	sinkPositionDeltaRequest.addArgumentByValue("positionSymbol", termCurrencySymbol)
	sinkPositionDeltaRequest.addArgumentByValue("deltaSequence", deltaSequence)
	sinkPositionDeltaRequest.addArgumentByValue("sourceTransaction", sourceTransactionNode)
	sinkPositionDeltaRequest.addPrimaryArgument(termCurrencyDeltaAmountsNode)
	context.issueAsyncRequest(sinkPositionDeltaRequest)
	deltaIdentifier = "delta:"+businessArea+POSITION_IDENTIFIER_DELIMITER+location+POSITION_IDENTIFIER_DELIMITER+lifecycleType+POSITION_IDENTIFIER_DELIMITER+account+POSITION_IDENTIFIER_DELIMITER+"OPEN"+POSITION_IDENTIFIER_DELIMITER+valueDate+POSITION_IDENTIFIER_DELIMITER+termCurrencySymbol+POSITION_IDENTIFIER_DELIMITER+deltaSequence
	b.addNode("positionDelta", deltaIdentifier)

} 

//Build response with list of position delta identifiers
context.createResponseFrom(b.getRoot())

// ************ Functions **************
def createSourceTransactionNode(transactionID, version) {
	HDSBuilder db = new HDSBuilder()
	db.pushNode("sourceTransaction")
	db.addNode("id", transactionID)
	db.addNode("version", version)
	return db.getRoot()
}

def generateOpenDeltaAmountsForCurrency(counterpartyBuysCurrency, symbol, amount, amountUSD) {
	HDSBuilder db = new HDSBuilder()
	db.pushNode("deltaAmounts")
	// SETTLEMENT
	db.pushNode("deltaAmount")
	def (String action, int value) = calculateOpenDeltaAmount(counterpartyBuysCurrency, amount)
	db.addNode("action", action)
	db.addNode("type", "SETTLEMENT")
	db.addNode("symbol", symbol)
	db.addNode("value", value)
	db.popNode()
	// BASE
	db.pushNode("deltaAmount")
	(action, value) = calculateOpenDeltaAmount(counterpartyBuysCurrency, amountUSD)
	db.addNode("action", action)
	db.addNode("type", "BASE")
	db.addNode("symbol", "USD")
	db.addNode("value", value)
	db.popNode()
	return db.getRoot()
}

def calculateOpenDeltaAmount(counterpartyBuysCurrency, settlementAmount) {
	action = counterpartyBuysCurrency ? "CREDIT" : "DEBIT"
	value = settlementAmount.round(0)+1  // Use conservative rounding up to nearest integer
	return [action, value]
}