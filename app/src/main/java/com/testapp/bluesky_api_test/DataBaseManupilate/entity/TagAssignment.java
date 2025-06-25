package com.testapp.bluesky_api_test.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(
	foreignKeys = {
		@ForeignKey(
			entity = BasePost.class,
			parentColumns = "id",
			childColumns = "post_id",
			onDelete = ForeignKey.CASCADE)
,
		@ForeignKey(
			entity = Tag.class,
			parentColumns = "id",
			childColumns = "tag_id",
			onDelete = ForeignKey.CASCADE)
	}
)
public class TagAssignment {



	@ColumnInfo(name = "post_id")
	private int post_id;

	@ColumnInfo(name = "tag_id")
	private int tag_id;

	public TagAssignment() {}

	public TagAssignment(int post_id, int tag_id) {
		this.post_id = post_id;
		this.tag_id = tag_id;
	}



	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	public int getTag_id() {
		return tag_id;
	}
	public void setTag_id(int tag_id) {
		this.tag_id = tag_id;
	}

}