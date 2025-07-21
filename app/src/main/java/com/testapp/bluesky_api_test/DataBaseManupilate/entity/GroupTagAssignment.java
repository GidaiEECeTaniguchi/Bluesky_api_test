package com.testapp.bluesky_api_test.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(
    primaryKeys = {"group_id", "tag_id"},
	foreignKeys = {
		@ForeignKey(
			entity = GroupEntity.class,
			parentColumns = "id",
			childColumns = "group_id",
			onDelete = ForeignKey.CASCADE)
,
		@ForeignKey(
			entity = Tag.class,
			parentColumns = "id",
			childColumns = "tag_id",
			onDelete = ForeignKey.CASCADE)
	},
	indices = {@Index("group_id"), @Index("tag_id")}
)
public class GroupTagAssignment {



	@ColumnInfo(name = "group_id")
	private int group_id;

	@ColumnInfo(name = "tag_id")
	private int tag_id;

	@Ignore
	public GroupTagAssignment() {}

	public GroupTagAssignment(int group_id, int tag_id) {
		this.group_id = group_id;
		this.tag_id = tag_id;
	}




	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public int getTag_id() {
		return tag_id;
	}
	public void setTag_id(int tag_id) {
		this.tag_id = tag_id;
	}

}