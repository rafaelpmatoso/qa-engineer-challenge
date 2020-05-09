package steps;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import object.Curso;
import object.Cursos;
import utils.PropertyReader;
import webdriver.DriverFactory;
import webdriver.DriverType;

public class Steps {

	private WebDriver driver = DriverFactory.getDriver(DriverType.CHROME);
	private WebDriverWait wait = new WebDriverWait(driver, 25);
	private int quantidadeCursosDisponiveisListagem;

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
		quantidadeCursosDisponiveisListagem = Integer.parseInt(driver.findElement(By
				.xpath("//a[@href='https://www.estrategiaconcursos.com.br/cursosPorProfessor/ena-loiola-800/']/../div"))
				.getText().replaceAll("[^\\d.]", ""));
		linkCursosEnaLoiola.click();
	}

	@E("^listar os cursos exibidos$")
	public void listarOsCursosExibidos() throws Throwable {
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
				Cursos.addCurso(new Curso(nomeCurso, linkDetalhes,
						new BigDecimal(valorTotalCurso.replace("R$ ", "").replace(".", "").replace(",", "."))));
			} else {
				Cursos.addCurso(new Curso(nomeCurso, linkDetalhes, null));
			}
		}
	}

	@Entao("^eu valido se o valor do curso na pagina de listagem e igual ao valor na pagina de detalhes$")
	public void euValidoSeOValorDoCursoNaPaginaDeListagemEIgualAoValorNaPaginaDeDetalhes() throws Throwable {
		for (Curso curso : Cursos.getCursos()) {
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
		for (Curso curso : Cursos.getCursos()) {
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

	@E("^que a quantidade de cursos exibidos na pagina de listagem e igual a quantidade de cursos na pagina de detalhes$")
	public void queAQuantidadeDeCursosExibidosNaPaginaDeListagemEIgualAQuantidadeDeCursosNaPaginaDeDetalhes()
			throws Throwable {
		int quantidadeCursosDetalhes = 0;
		for (Curso curso : Cursos.getCursos()) {
			if (!curso.getNomeCurso().contains("Assinatura")) {
				quantidadeCursosDetalhes++;
			}
		}
		Assert.assertTrue(
				"Quantidade cursos disponiveis na listagem:" + quantidadeCursosDisponiveisListagem
						+ "\nQuantidade de cursos na pagina de detalhes: " + quantidadeCursosDetalhes,
				quantidadeCursosDisponiveisListagem == quantidadeCursosDetalhes);
	}

	@Quando("^eu realizar a pesquisa na barra de busca$")
	public void euRealizarAPesquisaNaBarraDeBusca(DataTable dataTable) throws Throwable {
		for (Map<String, String> map : dataTable.asMaps()) {
			driver.findElement(By.name("q")).sendKeys(map.get("pesquisa") + Keys.ENTER);
		}
	}

	@E("^selecionar o filtro '(.*?)'$")
	public void selecionarOFiltro(String filtro) throws Throwable {
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'" + filtro + "')]")))
				.click();
	}

	@Entao("^os resultados serao exibidos de acordo com o filtro selecionado$")
	public void osResultadosSeraoExibidosDeAcordoComOFiltroSelecionado() throws Throwable {
		List<String> resultadosFiltro = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h1[@class='card-prod-title']/a")))
				.stream().map(x -> x.getText()).collect(Collectors.toList());
		for (String resultadoFiltro : resultadosFiltro) {
			assertTrue("Resultado " + resultadosFiltro + " não é de assinatura.",
					resultadoFiltro.toLowerCase().contains("assinatura"));
		}
	}

	@E("^ordernar os cursos em order crescente de valor$")
	public void ordernarOsCursosEmOrderCrescenteDeValor() throws Throwable {
		((JavascriptExecutor) driver).executeScript("document.querySelector('button[data-sort-by-price]').click();",
				"");
		List<BigDecimal> valores = driver.findElements(By.xpath("//span[@class='card-prod-price']")).stream()
				.map(x -> new BigDecimal(x.getText().replace("R$ ", "").replace(",", ".")))
				.collect(Collectors.toList());
		for (BigDecimal valor : valores) {
			System.out.println(valor);
		}

	}

}
