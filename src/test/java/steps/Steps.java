package steps;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import object.Curso;
import utils.PropertyReader;
import webdriver.DriverFactory;
import webdriver.DriverType;

public class Steps {

	private WebDriver driver = DriverFactory.getDriver(DriverType.CHROME);
	private WebDriverWait wait = new WebDriverWait(driver, 25);
	List<Curso> cursos = new ArrayList<>();

	@Dado("^que eu esteja na homepage da Estrategia Concursos$")
	public void queEuEstejaNaHomepageDaEstrategiaConcursos() throws Throwable {
		driver.get(PropertyReader.get("url"));
	}

	@Quando("^eu utilizar a busca 'Por professor'$")
	public void euUtilizarABuscaPorProfessor() throws Throwable {
		driver.findElement(By.xpath("//a[@href='https://www.estrategiaconcursos.com.br/cursos/professor/']")).click();
		wait.until(webdriver -> ((JavascriptExecutor) webdriver).executeScript("return document.readyState;", "")
				.equals("complete"));
	}

	@E("^acessar os cursos da professora 'Ena Loiola'$")
	public void acessarOsCursosDaProfessoraEnaLoiola() throws Throwable {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();",
				driver.findElement(By.xpath("//button[contains(text(),'Todos os professores')]")));
		WebElement linkCursosEnaLoiola = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//a[@href='https://www.estrategiaconcursos.com.br/cursosPorProfessor/ena-loiola-800/']")));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", linkCursosEnaLoiola);
		linkCursosEnaLoiola.click();
		obtemCursos();
	}

	@Entao("^eu valido se o valor do curso na pagina de listagem e igual ao valor na pagina de detalhes$")
	public void euValidoSeOValorDoCursoNaPaginaDeListagemEIgualAoValorNaPaginaDeDetalhes() throws Throwable {
		for (Curso curso : cursos) {
			if (curso.getValorTotalCurso() != null) {
				driver.get(curso.getLinkDetalhes());
				BigDecimal valorPaginaDetalhes = new BigDecimal(
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='value']")))
								.getText().replace("R$ ", "").replace(".", "").replace(",", "."));
				Assert.assertTrue(
						"Valor total na pagina de listagem: R$ " + curso.getValorTotalCurso()
								+ "\nValor total na pagina de detalhes: R$ " + valorPaginaDetalhes,
						curso.getValorTotalCurso().equals(valorPaginaDetalhes));
			}
		}
	}

	@E("^verifico que o total do valor parcelado do curso corresponde ao valor total$")
	public void verificoQueOTotalDoValorParceladoDoCursoCorrespondeAoValorTotal() throws Throwable {
		for (Curso curso : cursos) {
			if (curso.getValorTotalCurso() == null) {
				driver.get(curso.getLinkDetalhes());
				BigDecimal valorTotalPaginaDetalhes = new BigDecimal(
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='value']")))
								.getText().replace("R$ ", "").replace(".", "").replace(",", "."));
				BigDecimal valorParceladoPaginaDetalhes = new BigDecimal(
						driver.findElement(By.xpath("//div[@class='cur-details-shopping-installments']")).getText()
								.replace("ou 12x de R$ ", "").replace(".", "").replace(",", "."));
				Assert.assertTrue(
						"Nome do curso:" + curso.getNomeCurso() + "\nValor parcelado na pagina de listagem: 12 x R$ "
								+ valorParceladoPaginaDetalhes + " = R$ "
								+ valorParceladoPaginaDetalhes.multiply(new BigDecimal(12)).setScale(0, RoundingMode.UP)
								+ "\nValor total na pagina de detalhes: R$ " + valorTotalPaginaDetalhes,
						valorParceladoPaginaDetalhes.multiply(new BigDecimal(12)).setScale(0, RoundingMode.UP)
								.equals(valorTotalPaginaDetalhes));
			}
		}
	}

	private List<Curso> obtemCursos() {
		List<WebElement> secoesCurso = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//section[@class='card-prod || js-card-prod']")));
		for (int i = 1; i <= secoesCurso.size(); i++) {
			String nomeCurso = driver.findElement(By.xpath("(//h1[@class='card-prod-title'])[" + i + "]")).getText();
			String linkDetalhes = driver
					.findElement(By.xpath("(//section[@class='card-prod || js-card-prod']/a)[" + i + "]"))
					.getAttribute("href");
			String valorTotalCurso = driver.findElement(By.xpath("(//div[@class='card-prod-price'])[" + i + "]"))
					.getText();
			if (!valorTotalCurso.contains("cursos em até 12x de")) {
				cursos.add(new Curso(nomeCurso, linkDetalhes,
						new BigDecimal(valorTotalCurso.replace("R$ ", "").replace(".", "").replace(",", "."))));
			} else {
				cursos.add(new Curso(nomeCurso, linkDetalhes, null));
			}
		}
		return cursos;
	}

}
