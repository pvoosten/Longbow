package longbow;


import java.lang.ref.WeakReference;
import java.util.EventObject;

/**
 * The exception handling mechanism in Longbow is simply an Observer Pattern
 * implementation.
 * 
 * @author Philip van Oosten
 * 
 */
public class LongbowExceptionEvent extends EventObject {

	/**
	 * The context where the exception occurred.
	 * 
	 * 
	 * TODO: for security, this can be a proxy, so that input data etc. can't be
	 * retrieved when an exception occurs
	 */
	private final WeakReference<TransformationContext> transformationContext;

	public LongbowExceptionEvent(final LongbowException source, final TransformationContext context) {
		super(source);
		transformationContext = new WeakReference<TransformationContext>(context);
	}

	public TransformationContext getTransformationContext() {
		assert transformationContext != null;
		return transformationContext.get();
	}

}
