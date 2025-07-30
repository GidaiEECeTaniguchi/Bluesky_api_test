package com.aether.myaether.DataBaseManupilate.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.Index;

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
public class GroupRef {

	@PrimaryKey(autoGenerate = true)
	private int id;

	@ColumnInfo(name = "group_id")
	private int group_id;

	@ColumnInfo(name = "title")
	private String title;

	@ColumnInfo(name = "type")
	private String type;

	@ColumnInfo(name = "ref_path")
	private String ref_path;

	@ColumnInfo(name = "order_in_group")
	private int order_in_group; // 新しく追加

	@Ignore
	public GroupRef() {}

	// 新しいコンストラクタ
	public GroupRef(int group_id, String title, String type, String ref_path, int order_in_group) {
		this.group_id = group_id;
		this.title = title;
		this.type = type;
		this.ref_path = ref_path;
		this.order_in_group = order_in_group;
	}

	@Ignore
	public GroupRef(int group_id, String title, String type, String ref_path) {
		this(group_id, title, type, ref_path, 0); // デフォルト値を設定
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

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getRef_path() {
		return ref_path;
	}
	public void setRef_path(String ref_path) {
		this.ref_path = ref_path;
	}

	public int getOrder_in_group() {
		return order_in_group;
	}
	public void setOrder_in_group(int order_in_group) {
		this.order_in_group = order_in_group;
	}
}