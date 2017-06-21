package com.runwise.supply.pictakelist;

import java.io.Serializable;

public class PicTake  implements Serializable{

	private int id;
	private String materialId;
	private String name;

	private int duration;

	private int size;

	private String randomName;

	private String picPath;

	private boolean isSelect;

	private int type;

	private String createTime;

	private long createTimeLong;

	private String path;

	private long headerId;

	private String url;

	public static int PIC = 4; //图片

	public static int VID = 3; //视频

	private boolean isTakePic = false;//是否为使用摄像头拍照或录像

	private int width;

	private int height;

	private int position = -1;

	private String qiniuFileName ;

	private int pinnedType;
	private String pinnedTitle;

	private int section ;

	private boolean isNetResouce ;

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRandomName() {
		return randomName;
	}

	public void setRandomName(String randomName) {
		this.randomName = randomName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((picPath == null) ? 0 : picPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PicTake other = (PicTake) obj;
		if (picPath == null) {
			if (other.picPath != null)
				return false;
		} else if (!picPath.equals(other.picPath))
			return false;
		return true;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getCreateTimeLong() {
		return createTimeLong;
	}

	public void setCreateTimeLong(long createTimeLong) {
		this.createTimeLong = createTimeLong;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public boolean isTakePic() {
		return isTakePic;
	}

	public PicTake setIsTakePic(boolean isTakePic) {
		this.isTakePic = isTakePic;
		return this;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getQiniuFileName() {
		return qiniuFileName;
	}

	public PicTake setQiniuFileName(String qiniuFileName) {
		this.qiniuFileName = qiniuFileName;
		return this;
	}

	public int getPinnedType() {
		return pinnedType;
	}

	public void setPinnedType(int pinnedType) {
		this.pinnedType = pinnedType;
	}

	public void setSection(int section) {
		this.section = section;
	}
	public int getSection() {
		return section;
	}

	public String getPinnedTitle() {
		return pinnedTitle;
	}

	public void setPinnedTitle(String pinnedTitle) {
		this.pinnedTitle = pinnedTitle;
	}

	public boolean isNetResouce() {
		return isNetResouce;
	}

	public void setIsNetResouce(boolean isNetResouce) {
		this.isNetResouce = isNetResouce;
	}

	public String getMaterialId() {
		return materialId;
	}

	public PicTake setMaterialId(String materialId) {
		this.materialId = materialId;
		return this;
	}
}
