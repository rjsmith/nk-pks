import org.netkernel.layer0.nkf.INKFResponse
import org.netkernel.layer0.representation.IHDSNode
import org.netkernel.layer0.representation.impl.HDSBuilder
import java.util.UUID


// Generate source FX transaction
// Randomises some trade attributes

request = context.createRequest("active:updatePositions")

cptyUUIDs = ["b6a563a6-d82b-4b96-9b17-300c95e5d4f2",
	"0c29044b-ee93-46f3-8bef-26e36000a60e", 
	"c97c5ec0-be90-45f3-8191-1e20a2626868",
	"5039a5d7-0c5c-4ef5-a5f4-0276b55198dc"]
baseccys = ["USD", "EUR", "GBP", "AUD"]
termccys = ["JPY", "SGD", "HKD", "CAD"]
cptyBuysBase = ["true", "false"]


(trade, transactionID) = generateFXTrade()
request.addArgumentByValue("operand", trade)
context.issueRequest(request)

INKFResponse response = context.createResponseFrom(transactionID)
response.setExpiry(INKFResponse.EXPIRY_ALWAYS) // Force NK to always call this endpoint, instead of using representation cache

def generateFXTrade() {
	HDSBuilder b
	b = new HDSBuilder()
	def random = new Random();
	
	transactionID = UUID.randomUUID().toString()
	b.pushNode("trade")
	b.pushNode("lifecycle")
	b.addNode("businessArea", "EFX")
	b.addNode("location", "LDN")
	b.addNode("transactionID", transactionID)
	b.addNode("version", "1")
	b.addNode("lifecycleType", "PRE_STP")
	b.popNode()
	b.pushNode("parties")
	def i = random.nextInt(cptyUUIDs.size())
	b.addNode("counterparty", cptyUUIDs[i])
	b.popNode()
	b.pushNode("fxspotoutright")
	b.pushNode("baseCurrency")
	b.addNode("symbol", baseccys[random.nextInt(baseccys.size())])
	b.addNode("settlementAmount", random.nextInt(10000)+".00")
	b.addNode("settlementAmountUSD", random.nextInt(10000)+".00")
	b.popNode()
	b.pushNode("termCurrency")
	b.addNode("symbol", termccys[random.nextInt(termccys.size())])
	b.addNode("settlementAmount", random.nextInt(10000)+".00")
	b.addNode("settlementAmountUSD", random.nextInt(10000)+".00")
	b.popNode()
	b.addNode("counterpartyBuysBase", cptyBuysBase[random.nextInt(cptyBuysBase.size())])
	b.addNode("valueDate","2014-05-19")
	return [b.getRoot(), transactionID]
}


/*
 * 					<trade>
						<lifecycle>
							<businessArea>EFX</businessArea>
							<location>LDN</location>
							<transactionID>997cc866-3cd5-40c7-bb63-4e1c608b80f9</transactionID>
							<version>1</version>
							<lifecycleType>PRE_STP</lifecycleType>
						</lifecycle>
						<parties>
							<counterparty>ef79b770-b605-11e3-a5e2-0800200c9a66</counterparty>
						</parties>
						<fxspotoutright>
							<baseCurrency>
								<symbol>EUR</symbol>
								<settlementAmount>10000.00</settlementAmount>
								<settlementAmountUSD>13713.00</settlementAmountUSD>
							</baseCurrency>
							<termCurrency>
								<symbol>GBP</symbol>
								<settlementAmount>8166.69</settlementAmount>
								<settlementAmountUSD>13713.00</settlementAmountUSD>
							</termCurrency>
							<counterpartyBuysBase>true</counterpartyBuysBase>
							<valueDate>2014-05-28</valueDate>
						</fxspotoutright>
					</trade>	
*/
