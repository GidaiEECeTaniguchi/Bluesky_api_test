package com.aether.myaether.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity
public class Tag {

	@PrimaryKey(autoGenerate = false)
	private int id;

	@ColumnInfo(name = "name")
	private String name;

	@ColumnInfo(name = "scope")
	private String scope;

	@Ignore
	public Tag() {}

	public Tag(String name, String scope) {
		this.name = name;
		this.scope = scope;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}

}