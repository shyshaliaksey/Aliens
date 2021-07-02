package by.shyshaliaksey.webproject.model.entity;

public class Alien {

	private int id;
	private String name;
	private String smallDescription;
	private String bigDescription;
	private String imageUrl;
	
	public Alien(int id, String name, String smallDescription, String bigDescription, String imageUrl) {
		this.id = id;
		this.name = name;
		this.smallDescription = smallDescription;
		this.bigDescription = bigDescription;
		this.imageUrl = imageUrl;
	}
	
	public Alien(String name, String smallDescription, String bigDescription, String imageUrl) {
		this.name = name;
		this.smallDescription = smallDescription;
		this.bigDescription = bigDescription;
		this.imageUrl = imageUrl;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSmallDescription() {
		return smallDescription;
	}
	public void setSmallDescription(String smallDescription) {
		this.smallDescription = smallDescription;
	}
	public String getBigDescription() {
		return bigDescription;
	}
	public void setBigDescription(String bigDescription) {
		this.bigDescription = bigDescription;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((bigDescription == null) ? 0 : bigDescription.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((smallDescription == null) ? 0 : smallDescription.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Alien other = (Alien) obj;
		if (id != other.id) {
			return false;
		}
		if (bigDescription == null) {
			if (other.bigDescription != null) {
				return false;
			}
		} else if (!bigDescription.equals(other.bigDescription)) {
			return false;
		}
		if (imageUrl == null) {
			if (other.imageUrl != null) {
				return false;
			}
		} else if (!imageUrl.equals(other.imageUrl)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (smallDescription == null) {
			if (other.smallDescription != null) {
				return false;
			}
		} else if (!smallDescription.equals(other.smallDescription)) {
			return false;
		}
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Alien [alienId=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", smallDescription=");
		builder.append(smallDescription);
		builder.append(", bigDescription=");
		builder.append(bigDescription);
		builder.append(", imageUrl=");
		builder.append(imageUrl);
		builder.append("]");
		return builder.toString();
	}
	


	
	
	
	
}
