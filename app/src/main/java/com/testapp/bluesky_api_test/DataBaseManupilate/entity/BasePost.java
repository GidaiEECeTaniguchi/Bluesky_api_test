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
public class BasePost {

	@PrimaryKey(autoGenerate = false)
	private int id;

	@ColumnInfo(name = "user_id")
	private int user_id;

	@ColumnInfo(name = "content")
	private String content;

	@ColumnInfo(name = "created_at")
	private String created_at;

	public BasePost() {}

	public BasePost(int user_id, String content, String created_at) {
		this.user_id = user_id;
		this.content = content;
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

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

}