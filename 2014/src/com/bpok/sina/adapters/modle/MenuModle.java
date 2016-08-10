package com.bpok.sina.adapters.modle;

public class MenuModle {// 封装的菜单list显示的内容
	private String title;
	private int icon_sel;
	private int icon_unsel;
	private int indicator;

	/**
	 * @param title
	 * @param icon_sel
	 * @param icon_unsel
	 * @param indicator
	 */
	public MenuModle(String title, int icon_sel, int icon_unsel, int indicator) {
		super();
		this.title = title;
		this.icon_sel = icon_sel;
		this.icon_unsel = icon_unsel;
		this.indicator = indicator;
	}

	public MenuModle() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the icon_sel
	 */
	public int getIcon_sel() {
		return icon_sel;
	}

	/**
	 * @param icon_sel
	 *            the icon_sel to set
	 */
	public void setIcon_sel(int icon_sel) {
		this.icon_sel = icon_sel;
	}

	/**
	 * @return the icon_unsel
	 */
	public int getIcon_unsel() {
		return icon_unsel;
	}

	/**
	 * @param icon_unsel
	 *            the icon_unsel to set
	 */
	public void setIcon_unsel(int icon_unsel) {
		this.icon_unsel = icon_unsel;
	}

	/**
	 * @return the indicator
	 */
	public int getIndicator() {
		return indicator;
	}

	/**
	 * @param indicator
	 *            the indicator to set
	 */
	public void setIndicator(int indicator) {
		this.indicator = indicator;
	}

}