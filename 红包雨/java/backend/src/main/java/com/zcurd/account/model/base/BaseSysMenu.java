package com.zcurd.account.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseSysMenu<M extends BaseSysMenu<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public M setMenuName(java.lang.String menuName) {
		set("menu_name", menuName);
		return (M)this;
	}

	public java.lang.String getMenuName() {
		return get("menu_name");
	}

	public M setMenuUrl(java.lang.String menuUrl) {
		set("menu_url", menuUrl);
		return (M)this;
	}

	public java.lang.String getMenuUrl() {
		return get("menu_url");
	}

	public M setParentId(java.lang.Integer parentId) {
		set("parent_id", parentId);
		return (M)this;
	}

	public java.lang.Integer getParentId() {
		return get("parent_id");
	}

	public M setIcon(java.lang.String icon) {
		set("icon", icon);
		return (M)this;
	}

	public java.lang.String getIcon() {
		return get("icon");
	}

	public M setOrderNum(java.lang.Integer orderNum) {
		set("order_num", orderNum);
		return (M)this;
	}

	public java.lang.Integer getOrderNum() {
		return get("order_num");
	}

	public M setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
		return (M)this;
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

}
