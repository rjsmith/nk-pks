import org.netkernel.layer0.nkf.INKFRequestContext

/**
 * sendMultipleTrades.groovy
 * 
 * Sends sequence of source transaction requests into PKS
 * 
 */

 
for (i in 0..4) {
	context.source("res:/pks/sendrandomtrade")
	sleep (1000)
}
	
