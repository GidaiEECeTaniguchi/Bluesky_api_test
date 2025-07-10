package com.testapp.bluesky_api_test.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(
	foreignKeys = {
		@ForeignKey(
			entity = User.class,
			parentColumns = "id",
			childColumns = "user_id",
			onDelete = ForeignKey.CASCADE)
	}
)
public class GroupEntity {

	@PrimaryKey(autoGenerate = false)
	private int id;

	@ColumnInfo(name = "user_id")
	private int user_id;

	@ColumnInfo(name = "name")
	private String name;

	@ColumnInfo(name = "created_at")
	private String created_at;

	public GroupEntity() {}

	public GroupEntity(int id, int user_id, String name, String created_at) {
		this.id = id;
		this.user_id = user_id;
		this.name = name;
		this.created_at = created_at;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

}