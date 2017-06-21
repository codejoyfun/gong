package com.runwise.supply.pictakelist;

import java.util.List;

public class PicFloder {
        private String floderPath;
        private String ImageFace;
        private String showName;
        private List<PicTake> picList;
        private int picCount;
        private boolean isSelect;
        public PicFloder(String showName, String floderPath,String ImageFace) {
        	this.floderPath = floderPath;
        	this.ImageFace = ImageFace;
        	this.showName = showName;
        }
		public String getFloderPath() {
			return floderPath;
		}
		public void setFloderPath(String floderPath) {
			this.floderPath = floderPath;
		}
		public String getImageFace() {
			return ImageFace;
		}
		public int getPicCount() {
			return picCount;
		}
		public void setPicCount(int picCount) {
			this.picCount = picCount;
		}
		public void setImageFace(String imageFace) {
			ImageFace = imageFace;
		}
		public List<PicTake> getPicList() {
			return picList;
		}
		public void setPicList(List<PicTake> picList) {
			this.picList = picList;
		}
		public String getShowName() {
			return showName;
		}
		public void setShowName(String showName) {
			this.showName = showName;
		}
		public boolean isSelect() {
			return isSelect;
		}
		public void setSelect(boolean isSelect) {
			this.isSelect = isSelect;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((floderPath == null) ? 0 : floderPath.hashCode());
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
			PicFloder other = (PicFloder) obj;
			if (floderPath == null) {
				if (other.floderPath != null)
					return false;
			} else if (!floderPath.equals(other.floderPath))
				return false;
			return true;
		}
        
}
