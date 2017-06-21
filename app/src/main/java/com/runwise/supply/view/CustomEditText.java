/**
 * 
 */

package com.runwise.supply.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class CustomEditText extends EditText implements View.OnTouchListener, View.OnFocusChangeListener {
	private Context context;
	private boolean useCustomInputMethod;
	private Runnable showCustomInputMethod;
	private Runnable hideCustomInputMethod;
	private boolean showedCustomInputMethod;
	
    public CustomEditText(Context context) {
    	super(context, null);
    	this.context = context;
    	setOnTouchListener(this);
    	setOnFocusChangeListener(this);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	this.context = context;
    	setOnTouchListener(this);
    	setOnFocusChangeListener(this);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    	this.context = context;
    	setOnTouchListener(this);
    	setOnFocusChangeListener(this);
    }
    
    public void setUseCustomInputMethod(boolean use) {
    	useCustomInputMethod = use;
    }
    
    public void setShowCustomInputMethod(Runnable run) {
    	showCustomInputMethod = run;
    }
    
    public void setHideCustomInputMethod(Runnable run) {
    	hideCustomInputMethod = run;
    }
    
    private void showCustomInputMethod() {
    	InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(), 0);
		if(showCustomInputMethod != null) showCustomInputMethod.run();
		showedCustomInputMethod = true;
    }
    
    public void appendText(String text) {
    	String now = getText().toString();
    	setText(now + text);
    	setSelection(getText().length());
    }
    
    public void removeLastText() {
    	String now = getText().toString();
    	if(now.length() > 0) {
    		setText(now.substring(0, now.length() - 1));
    		setSelection(getText().length());
    	}
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(useCustomInputMethod) {
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				showCustomInputMethod();
				requestFocus();
			}
			if(event.getAction() == MotionEvent.ACTION_UP) {
				if(showedCustomInputMethod) return true;
			}
			return false;
		} else {
			return false;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(useCustomInputMethod) {
			if(hasFocus) {
				showCustomInputMethod();
			} else {
				if(hideCustomInputMethod != null) hideCustomInputMethod.run();
				showedCustomInputMethod = false;
			}
		}
	}
}
