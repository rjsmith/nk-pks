import org.netkernel.layer0.nkf.INKFRequest
import org.netkernel.layer0.representation.IHDSNode
import org.netkernel.layer0.representation.impl.HDSBuilder

/**
 * Builds a DynamicImport list of spaces containing position builders
 * 
 * <config>
 *     <space>
 *       <uri>urn:org:netkernel:some:module:space</uri>
 *     </space>
 *   </config>
 */
builders = context.source("active:discoverPositionBuilderEndpoints")

req = context.createRequest("active:xslt2")
req.addArgumentByValue("operand", builders)
req.addArgument("operator", "res:/uk/co/rsbatechnology/pks/frontend/builderSpaceImports-style.xsl")
rep=context.issueRequest(req)

context.createResponseFrom(rep);


