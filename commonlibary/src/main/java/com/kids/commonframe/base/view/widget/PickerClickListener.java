package com.kids.commonframe.base.view.widget;
/**
 *所有picker类型的点击监听 
 * @author dichao
 */
public interface PickerClickListener {
	/**
	 * 当前选中项目，当前选中position
	 * @param currentStr
	 * @param currentPosition
	 */
    public void doPickClick(String currentStr, int currentPosition);
}
