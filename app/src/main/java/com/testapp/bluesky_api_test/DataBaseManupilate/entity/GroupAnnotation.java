package com.testapp.bluesky_api_test.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(
	foreignKeys = {
		@ForeignKey(
			entity = GroupEntity.class,
			parentColumns = "id",
			childColumns = "group_id",
			onDelete = ForeignKey.CASCADE)
	}
)
public class GroupAnnotation {

	@PrimaryKey(autoGenerate = false)
	private int id;

	@ColumnInfo(name = "group_id")
	private int group_id;

	@ColumnInfo(name = "concept")
	private String concept;

	@ColumnInfo(name = "description")
	private String description;

	@ColumnInfo(name = "created_at")
	private String created_at;

	public GroupAnnotation() {}

	public GroupAnnotation(int group_id, String concept, String description, String created_at) {
		this.group_id = group_id;
		this.concept = concept;
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

	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
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