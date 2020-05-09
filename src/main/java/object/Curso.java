package object;

import java.math.BigDecimal;

public class Curso {

	private String nomeCurso;
	private String linkDetalhes;
	private BigDecimal valorTotalCurso;

	public Curso(String nomeCurso, String linkDetalhes, BigDecimal valorTotalCurso) {
		this.nomeCurso = nomeCurso;
		this.linkDetalhes = linkDetalhes;
		this.valorTotalCurso = valorTotalCurso;
	}

	public String getNomeCurso() {
		return nomeCurso;
	}

	public void setNomeCurso(String nomeCurso) {
		this.nomeCurso = nomeCurso;
	}

	public String getLinkDetalhes() {
		return linkDetalhes;
	}

	public void setLinkDetalhes(String linkDetalhes) {
		this.linkDetalhes = linkDetalhes;
	}

	public BigDecimal getValorTotalCurso() {
		return valorTotalCurso;
	}

	public void setValorTotalCurso(BigDecimal valorTotalCurso) {
		this.valorTotalCurso = valorTotalCurso;
	}

	@Override
	public String toString() {
		return "Curso [nomeCurso=" + nomeCurso + ", linkDetalhes=" + linkDetalhes + ", valorTotalCurso="
				+ valorTotalCurso + "]";
	}

}
