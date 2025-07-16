package com.testapp.bluesky_api_test.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(indices = {@Index(value = {"did"}, unique = true)})
public class User {

	@PrimaryKey(autoGenerate = true)
	private int id;

	@ColumnInfo(name = "name")
	private String name;

	@NonNull
	@ColumnInfo(name = "did")
	private String did;

	@Ignore
	public User() {}

	public User(String name, @NonNull String did) {
		this.name = name;
		this.did = did;
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

	@NonNull
	public String getDid() {
		return did;
	}
	public void setDid(@NonNull String did) {
		this.did = did;
	}
}