package com.bpok.sina.adapters.modle;

public class PopItem {
	private String titleString;
	private int iconSrc;
	// 有新更新的指示器
	private int tipSrc;

	public PopItem() {
	}

	/**
	 * @param titleString
	 * @param iconSrc
	 * @param tipSrc
	 */
	public PopItem(String titleString, int iconSrc, int tipSrc) {
		super();
		this.titleString = titleString;
		this.iconSrc = iconSrc;
		this.tipSrc = tipSrc;
	}

	public String getTitleString() {
		return titleString;
	}

	public void setTitleString(String titleString) {
		this.titleString = titleString;
	}

	public int getIconSrc() {
		return iconSrc;
	}

	public void setIconSrc(int iconSrc) {
		this.iconSrc = iconSrc;
	}

	public int getTipSrc() {
		return tipSrc;
	}

	public void setTipSrc(int tipSrc) {
		this.tipSrc = tipSrc;
	}

}
