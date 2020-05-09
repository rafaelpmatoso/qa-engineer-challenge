package object;

import java.util.ArrayList;
import java.util.List;

public class Cursos {

	private static List<Curso> lista = new ArrayList<>();

	public static void setCursos(List<Curso> cursos) {
		Cursos.lista = cursos;
	}

	public static void addCurso(Curso curso) {
		lista.add(curso);
	}

	public static List<Curso> getCursos() {
		return lista;
	}

}
