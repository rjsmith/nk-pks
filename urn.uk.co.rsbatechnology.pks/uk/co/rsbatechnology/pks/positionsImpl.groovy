/** 
 * Script to return list of position values
 */

 import org.netkernel.layer0.representation.*
 import org.netkernel.layer0.representation.impl.*;
 
 IHDSNode root = context.source("arg:operand", IHDSNode.class)
 
 println(root);
 
 b=new HDSBuilder();
 b.pushNode("positions")
 
 /*
 for(i=0; i<positionIdentifiers.size(); i++)
 {   
	 b.pushNode("position",context.source(cells[i]))
	 b.addNode("@id",positionIdentifiers[i])
	 b.popNode();
 }
 */
 context.createResponseFrom(b.getRoot())



