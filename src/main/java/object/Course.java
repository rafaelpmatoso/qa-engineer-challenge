package object;

import java.math.BigDecimal;

public class Course {

	private String courseName;
	private String detailsLink;
	private BigDecimal courseTotalPrice;

	public Course(String courseName, String detailsLink, BigDecimal courseTotalPrice) {
		this.courseName = courseName;
		this.detailsLink = detailsLink;
		this.courseTotalPrice = courseTotalPrice;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getDetailsLink() {
		return detailsLink;
	}

	public void setDetailsLink(String detailsLink) {
		this.detailsLink = detailsLink;
	}

	public BigDecimal getCourseTotalPrice() {
		return courseTotalPrice;
	}

	public void setCourseTotalPrice(BigDecimal courseTotalPrice) {
		this.courseTotalPrice = courseTotalPrice;
	}

	@Override
	public String toString() {
		return "Course [courseName=" + courseName + ", detailsLink=" + detailsLink + ", courseTotalPrice="
				+ courseTotalPrice + "]";
	}

}
