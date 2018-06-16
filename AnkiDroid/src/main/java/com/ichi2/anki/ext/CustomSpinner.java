package com.ichi2.anki.ext;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
* https://stackoverflow.com/questions/18447063/spinner-get-state-or-get-notified-when-opens
 * @author chenxuanlong
 * @date 2018/6/16
 */
public class CustomSpinner extends android.support.v7.widget.AppCompatSpinner {
	
	public CustomSpinner(Context context) {
		super(context);
	}
	
	public CustomSpinner(Context context, int mode) {
		super(context, mode);
	}
	
	public CustomSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
		super(context, attrs, defStyleAttr, mode);
	}
	
	public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
		super(context, attrs, defStyleAttr, mode, popupTheme);
	}
	
	/**
	 * An interface which a client of this Spinner could use to receive
	 * open/closed events for this Spinner.
	 */
	public interface OnSpinnerEventsListener {
		
		/**
		 * Callback triggered when the spinner was opened.
		 */
		void onSpinnerOpened(Spinner spinner);
		
		/**
		 * Callback triggered when the spinner was closed.
		 */
		void onSpinnerClosed(Spinner spinner);
		
	}
	
	private OnSpinnerEventsListener mListener;
	private boolean mOpenInitiated = false;
	
	// implement the Spinner constructors that you need
	
	private boolean mToggleFlag = true;
	
	@Override
	public int getSelectedItemPosition() {
		// this toggle is required because this method will get called in other
		// places too, the most important being called for the
		// OnItemSelectedListener
		if (!mToggleFlag) {
			int selectedItemPosition = super.getSelectedItemPosition();
			selectedItemPosition -= 5;
			if (selectedItemPosition < 0) {
				selectedItemPosition = 0;
			}
			return selectedItemPosition;
		}
		return super.getSelectedItemPosition();
	}
	
	@Override
	public boolean performClick() {
		
		// this method shows the list of elements from which to select one.
		// we have to make the getSelectedItemPosition to return 0 so you can
		// fool the Spinner and let it think that the selected item is the first
		// element
		mToggleFlag = false;
		
		// register that the Spinner was opened so we have a status
		// indicator for when the container holding this Spinner may lose focus
		mOpenInitiated = true;
		if (mListener != null) {
			mListener.onSpinnerOpened(this);
		}
		boolean result = super.performClick();
		mToggleFlag = true;
		return result;
	}
	
	/**
	 * Register the listener which will listen for events.
	 */
	public void setSpinnerEventsListener(
			OnSpinnerEventsListener onSpinnerEventsListener) {
		mListener = onSpinnerEventsListener;
	}
	
	/**
	 * Propagate the closed Spinner event to the listener from outside if needed.
	 */
	public void performClosedEvent() {
		mOpenInitiated = false;
		if (mListener != null) {
			mListener.onSpinnerClosed(this);
		}
	}
	
	/**
	 * A boolean flag indicating that the Spinner triggered an open event.
	 *
	 * @return true for opened Spinner
	 */
	public boolean hasBeenOpened() {
		return mOpenInitiated;
	}
	
	public void onWindowFocusChanged (boolean hasFocus) {
		if (hasBeenOpened() && hasFocus) {
			performClosedEvent();
		}
	}
	
}