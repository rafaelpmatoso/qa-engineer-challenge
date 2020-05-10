package steps;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
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
	private List<JSONObject> resultadosPagina;

	@Dado("^que eu esteja na homepage da Estrategia Concursos$")
	public void queEuEstejaNaHomepageDaEstrategiaConcursos() throws Throwable {
		driver.get(PropertyReader.get("url"));
	}

	@Quando("^eu utilizar a busca '(.*?)'$")
	public void euUtilizarABuscaPorProfessor(String tipoBusca) throws Throwable {
		driver.findElement(By.xpath("//a[@href='https://www.estrategiaconcursos.com.br/cursos/professor/']")).click();
		wait.until(webdriver -> ((JavascriptExecutor) webdriver).executeScript("return document.readyState;", "")
				.equals("complete"));
	}

	@E("^acessar os cursos da professora '(.*?)'$")
	public void acessarOsCursosDaProfessoraEnaLoiola(String parametroBusca) throws Throwable {
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
		List<WebElement> cursosProfessor = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
				By.xpath("//section[@class='card-prod || js-card-prod']/h1/a[not(contains(text(), 'Assinatura'))]")));
		for (int i = 1; i <= cursosProfessor.size(); i++) {
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
				assertTrue(
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
				assertTrue(
						"Curso: " + curso.getNomeCurso() + "\nValor parcelado na pagina de listagem: 12 x R$ "
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
		int quantidadeCursosDetalhes = Cursos.getCursos().stream().filter(x -> !x.getNomeCurso().contains("Assinatura"))
				.collect(Collectors.toList()).size();
		assertTrue(
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
			assertTrue("Resultado " + resultadoFiltro + " não é plano de assinatura.",
					resultadoFiltro.toLowerCase().contains("assinatura"));
		}
	}

	@Entao("^valido que todos os resultados retornados pela API sao exibidos$")
	public void validoQueTodosOsResultadosRetornadosPelaAPISaoExibidos() throws Throwable {
		List<JSONObject> resultadosAPI = listaResultadosAPI();
		assertTrue("Total de resultados na pagina: " + resultadosPagina.size() + "\nTotal de resultados API: "
				+ resultadosAPI.size(), resultadosAPI.size() == resultadosPagina.size());
	}

	private List<JSONObject> listaResultadosAPI() {
		List<JSONObject> resultadosAPI = new ArrayList<>();
		boolean hasMore = true;
		int i = 1;
		while (hasMore) {
			JSONObject jsonResponse = new JSONObject(
					given().queryParams("q", "oab").queryParam("p", i).queryParam("tipo", "cursos").when()
							.get("https://www.estrategiaconcursos.com.br/pesquisa/main/json/").then().statusCode(200)
							.extract().body().asString());
			JSONArray responseJSON = jsonResponse.getJSONArray("result");
			responseJSON.forEach(x -> resultadosAPI.add(new JSONObject(x.toString())));
			hasMore = jsonResponse.getBoolean("hasMore");
			i++;
		}
		return resultadosAPI;
	}

	@E("^listar os resultados exibidos na pagina$")
	public void listarOsResultadosExibidosNaPagina() throws Throwable {
		resultadosPagina = carregaTodosResultadosPagina();

	}

	private List<JSONObject> listaResultadosCarregados() {
		List<JSONObject> resultadosPaginaWeb = new ArrayList<>();
		for (int i = 1; i <= driver.findElements(By.xpath("//section[@class='card-prod']")).size(); i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("nome",
					driver.findElement(By.xpath("(//section[@class='card-prod']/h1/a)[" + i + "]")).getText());
			String valorParcelas = driver.findElement(By.xpath("(//section[@class='card-prod']/span)[" + i + "]"))
					.getText();
			if (valorParcelas.equals("R$ 0,00")) {
				jsonObject.put("valor", "0.00");
				jsonObject.put("parcelas", "");
				jsonObject.put("valor_parcela", JSONObject.NULL);
			} else {
				BigDecimal valorParcela = new BigDecimal(valorParcelas.split("x R\\$ ")[1].replace(",", ".").trim());
				BigDecimal quantidadeParcelas = new BigDecimal(
						valorParcelas.split("x R\\$ ")[0].replace(",", ".").trim());
				jsonObject.put("valor", valorParcela.multiply(quantidadeParcelas).toString());
				jsonObject.put("parcelas", quantidadeParcelas.toString());
				jsonObject.put("valor_parcela", valorParcela.toString());
			}
			jsonObject.put("url_comprar", driver
					.findElement(By.xpath("(//section[@class='card-prod']/a[2])[" + i + "]")).getAttribute("href"));
			resultadosPaginaWeb.add(jsonObject);
		}
		return resultadosPaginaWeb;

	}

	private List<JSONObject> carregaTodosResultadosPagina() throws InterruptedException {
		boolean maisResultados = true;
		while (maisResultados) {
			Thread.sleep(2000);
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight);", "");
			WebElement botaoMaisResultados = wait
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='button-more']")));
			try {
				botaoMaisResultados.click();
			} catch (ElementNotInteractableException e) {
				maisResultados = false;
			}
		}
		return listaResultadosCarregados();
	}

}
