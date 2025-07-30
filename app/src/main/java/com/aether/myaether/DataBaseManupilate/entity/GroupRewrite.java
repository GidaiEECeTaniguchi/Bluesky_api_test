package com.aether.myaether.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(
	foreignKeys = {
		@ForeignKey(
			entity = GroupEntity.class,
			parentColumns = "id",
			childColumns = "group_id",
			onDelete = ForeignKey.CASCADE)
	},
	indices = {@Index("group_id")}
)
public class GroupRewrite {

	@PrimaryKey(autoGenerate = false)
	private int id;

	@ColumnInfo(name = "group_id")
	private int group_id;

	@ColumnInfo(name = "matcher_text")
	private String matcher_text;

	@ColumnInfo(name = "rewritten_text")
	private String rewritten_text;

	@ColumnInfo(name = "description")
	private String description;

	@ColumnInfo(name = "created_at")
	private String created_at;

	@Ignore
	public GroupRewrite() {}

	public GroupRewrite(int group_id, String matcher_text, String rewritten_text, String description, String created_at) {
		this.group_id = group_id;
		this.matcher_text = matcher_text;
		this.rewritten_text = rewritten_text;
		this.description = description;
		this.created_at = created_at;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public String getMatcher_text() {
		return matcher_text;
	}
	public void setMatcher_text(String matcher_text) {
		this.matcher_text = matcher_text;
	}

	public String getRewritten_text() {
		return rewritten_text;
	}
	public void setRewritten_text(String rewritten_text) {
		this.rewritten_text = rewritten_text;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

}