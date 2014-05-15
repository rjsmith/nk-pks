/**
 * Issues a test request provided by an argument, then recurses through exception
 * to find one that matches a given exception id.
 * 
 * Returns response of "true" if it finds a matching NKFException, else "false"
 * 
 * Designed for use in an XUnit test.
 * Use <true /> built-in assert to verify exception has matched
 */

 import org.netkernel.layer0.nkf.NKFException
 
 
 expectedExceptionID = context.source("arg:expectedExceptionID")

 expectedMatchesActual = false
 try {
	 // Issue test request, expect it to throw an exception
	 context.source("arg:testRequest")
 }
 catch (Throwable t)
 {
	 // Recursively search through exception stack to find a NKFException that matches the expectedExceptionID
	 while (t != null)
	 {
		 if (t instanceof NKFException)
		 {
			 id = ((NKFException)t).getId()
			 expectedMatchesActual = expectedExceptionID.equals(id)
			 if (expectedMatchesActual)
				 break
		 }
		 t = t.getCause()
	 }
 }
 context.createResponseFrom(expectedMatchesActual)
 


