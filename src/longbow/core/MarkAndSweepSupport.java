/*
 * Copyright 2007-2008 Philip van Oosten (Mentoring Systems BVBA)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package longbow.core;


import java.util.Map;
import java.util.WeakHashMap;

import longbow.MarkAndSweepEvent;
import longbow.MarkAndSweepListener;
import longbow.TransformationContext;

import org.apache.log4j.Logger;

/**
 * Provides basic operations {@link TransformationContext}s can use to
 * participate in mark and sweep.
 * 
 * @author Philip van Oosten
 * 
 */
public class MarkAndSweepSupport {

	private static final Logger logger = Logger.getLogger(MarkAndSweepSupport.class);

	private long serialNumber;

	private boolean markAndSweepListenersEnabled;

	private final TransformationContext source;

	private final Map<MarkAndSweepListener, Object> listeners;

	private volatile LastEvent lastEvent;

	/**
	 * Creates a new instance of {@link MarkAndSweepSupport}
	 * 
	 * @param source
	 *            The supported context, the context events originate from
	 * 
	 */
	public MarkAndSweepSupport(final TransformationContext source) {
		this.source = source;
		listeners = new WeakHashMap<MarkAndSweepListener, Object>();
		serialNumber = 0x0L;
		lastEvent = LastEvent.MARKED;
	}

	/**
	 * Adds a listener
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public synchronized void addMarkAndSweepListener(final MarkAndSweepListener listener) {
		if (listener != null) {
			listeners.put(listener, null);
		}
	}

	/**
	 * Checks if listeners will be notified of marks and sweeps of the supported
	 * context.
	 * 
	 * @return {@code true} if listeners wil be notified of marks and sweeps of
	 *         the supported context.
	 */
	public boolean isMarkAndSweepListenersEnabled() {
		return markAndSweepListenersEnabled;
	}

	/**
	 * Checks if the supported context is valid.
	 * 
	 * @return if the supported context is valid.
	 */
	public boolean isValid() {
		return lastEvent == LastEvent.SWEPT;
	}

	/**
	 * Checks if the supported context is trying to become valid.
	 * 
	 * @return if the supported context is validating
	 */
	public boolean isValidating() {
		return lastEvent == LastEvent.SWEEPING;
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public synchronized void removeMarkAndSweepListener(final MarkAndSweepListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Enables or disables listening to marks and sweeps for transformations.
	 * Disabling listeners can be done for performance reasons.
	 * 
	 * @param markAndSweepListenersEnabled
	 *            if mark and sweep events should be enabled for external
	 *            objects.
	 */
	public void setListenersEnabled(final boolean markAndSweepListenersEnabled) {
		this.markAndSweepListenersEnabled = markAndSweepListenersEnabled;
	}

	/**
	 * Notify listeners of the supported context that it has been marked.
	 */
	void fireMarked() {
		if (lastEvent != LastEvent.MARKING) {
			final IllegalStateException e = new IllegalStateException();
			logger.fatal("Can't have marked if last event wasn't MARKING, but " + lastEvent, e);
			throw e;
		}
		lastEvent = LastEvent.MARKED;
		if (markAndSweepListenersEnabled) {
			final MarkAndSweepEvent event = new MarkAndSweepEvent(source);
			for (final MarkAndSweepListener listener : listeners.keySet()) {
				listener.marked(event);
			}
		}
		logger.trace(source + " is " + lastEvent.name());
	}

	/**
	 * Fire events for a new mark, with a new serial number.
	 */
	void fireMarking() {
		fireMarking(serialNumber + 1L);
	}

	/**
	 * Propagate a mark received from a precursor
	 * 
	 * @param serialNumber
	 */
	void fireMarking(final long serialNumber) {
		if (this.serialNumber != serialNumber) {
			this.serialNumber = serialNumber;
			lastEvent = LastEvent.MARKING;
			if (markAndSweepListenersEnabled) {
				final MarkAndSweepEvent event = new MarkAndSweepEvent(source);
				for (final MarkAndSweepListener listener : listeners.keySet()) {
					listener.marking(event);
				}
			}
		}
		logger.trace(source + " is " + lastEvent.name());
	}

	/**
	 * Notifies listeners that the supported context is sweeping.
	 */
	void fireSweeping() {
		if (lastEvent != LastEvent.MARKED) {
			final IllegalStateException e = new IllegalStateException();
			logger.fatal("Can't start sweeping if last event was not MARKED, but " + lastEvent, e);
			throw e;
		}
		lastEvent = LastEvent.SWEEPING;
		if (markAndSweepListenersEnabled) {
			final MarkAndSweepEvent event = new MarkAndSweepEvent(source);
			for (final MarkAndSweepListener listener : listeners.keySet()) {
				listener.sweeping(event);
			}
		}
		logger.trace(source + " is " + lastEvent.name());
	}

	//	void fireSweepInterrupted(final LongbowException exception) {
	//		if (lastEvent != LastEvent.SWEEPING) {
	//			final IllegalStateException e = new IllegalStateException(exception);
	//			logger.fatal("Sweep can't be interrupted if last event was not SWEEPING, but " + lastEvent, e);
	//			throw e;
	//		}
	//		lastEvent = LastEvent.MARKED;
	//		if (markAndSweepListenersEnabled) {
	//			final MarkAndSweepEvent event = new MarkAndSweepEvent(source);
	//			for (final MarkAndSweepListener listener : listeners.keySet()) {
	//				listener.sweepInterrupted(event);
	//			}
	//		}
	//		logger.trace(source + " is " + lastEvent.name());
	//	}

	/**
	 * Notifies listeners that the supported context is swept.
	 */
	void fireSwept() {
		if (lastEvent != LastEvent.SWEEPING) {
			final IllegalStateException e = new IllegalStateException();
			logger.fatal("Can't have swept if last event was not SWEEPING, but " + lastEvent, e);
			throw e;
		}
		lastEvent = LastEvent.SWEPT;
		if (markAndSweepListenersEnabled) {
			final MarkAndSweepEvent event = new MarkAndSweepEvent(source);
			for (final MarkAndSweepListener listener : listeners.keySet()) {
				listener.swept(event);
			}
		}
		logger.trace(source + " is " + lastEvent.name());
	}

	private enum LastEvent {
		/**
		 * The supported context is invalid and followers are being notified of
		 * that.
		 */
		MARKING,
		/**
		 * The supported context is invalid
		 */
		MARKED,
		/**
		 * The supported context is trying to become valid
		 */
		SWEEPING,
		/**
		 * The supported context is valid
		 */
		SWEPT;
	}

}
