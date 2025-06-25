package com.testapp.bluesky_api_test.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

@Entity(
	foreignKeys = {
		@ForeignKey(
			entity = GroupEntity.class,
			parentColumns = "id",
			childColumns = "group_id",
			onDelete = ForeignKey.CASCADE)
,
		@ForeignKey(
			entity = User.class,
			parentColumns = "id",
			childColumns = "user_id",
			onDelete = ForeignKey.CASCADE)
	}
)
public class GroupMember {




	@ColumnInfo(name = "group_id")
	private int group_id;

	@ColumnInfo(name = "user_id")
	private int user_id;

	@ColumnInfo(name = "role")
	private int role;

	public GroupMember() {}

	public GroupMember(int group_id, int user_id, int role) {
		this.group_id = group_id;
		this.user_id = user_id;
		this.role = role;
	}



	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}

}