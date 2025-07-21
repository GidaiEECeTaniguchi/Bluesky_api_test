package com.testapp.bluesky_api_test.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;

@Entity(
    primaryKeys = {"group_id", "post_id"},
	foreignKeys = {
		@ForeignKey(
			entity = GroupEntity.class,
			parentColumns = "id",
			childColumns = "group_id",
			onDelete = ForeignKey.CASCADE),
		@ForeignKey(
			entity = BasePost.class,
			parentColumns = "id",
			childColumns = "post_id",
			onDelete = ForeignKey.CASCADE)
	}
)
public class GroupMember {

	@ColumnInfo(name = "group_id")
	private int group_id;

	@ColumnInfo(name = "post_id")
	private int post_id;

	@ColumnInfo(name = "order")
	private int order;

	public GroupMember() {}

	public GroupMember(int group_id, int post_id, int order) {
		this.group_id = group_id;
		this.post_id = post_id;
		this.order = order;
	}

	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

}