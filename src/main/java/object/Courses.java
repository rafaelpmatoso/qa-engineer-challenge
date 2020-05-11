package object;

import java.util.ArrayList;
import java.util.List;

public class Courses {

	private static List<Course> courseslist = new ArrayList<>();

	public static void setCourse(List<Course> cursos) {
		Courses.courseslist = cursos;
	}

	public static void addCourse(Course curso) {
		courseslist.add(curso);
	}

	public static List<Course> getCourse() {
		return courseslist;
	}

}
