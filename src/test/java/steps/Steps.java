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
import object.Course;
import object.Courses;
import utils.PropertyReader;
import webdriver.DriverFactory;
import webdriver.DriverType;

public class Steps {

	private WebDriver driver = DriverFactory.getDriver(DriverType.CHROME);
	private WebDriverWait wait = new WebDriverWait(driver, 25);
	private int totalCoursesAvailableInListPage;
	private Long webPageResults;

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
	public void acessarOsCursosDaProfessoraEnaLoiola(String searchParameter) throws Throwable {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();",
				driver.findElement(By.xpath("//button[contains(text(),'Todos os professores')]")));
		WebElement professorCoursesLink = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//a[@href='https://www.estrategiaconcursos.com.br/cursosPorProfessor/ena-loiola-800/']")));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", professorCoursesLink);
		totalCoursesAvailableInListPage = Integer.parseInt(driver.findElement(By
				.xpath("//a[@href='https://www.estrategiaconcursos.com.br/cursosPorProfessor/ena-loiola-800/']/../div"))
				.getText().replaceAll("[^\\d.]", ""));
		professorCoursesLink.click();
	}

	@E("^listar os cursos exibidos$")
	public void listarOsCursosExibidos() throws Throwable {
		List<WebElement> professorCourses = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
				By.xpath("//section[@class='card-prod || js-card-prod']/h1/a[not(contains(text(), 'Assinatura'))]")));
		for (int i = 1; i <= professorCourses.size(); i++) {
			String courseName = driver.findElement(By.xpath("(//h1[@class='card-prod-title'])[" + i + "]")).getText();
			String detailsLink = driver
					.findElement(By.xpath("(//section[@class='card-prod || js-card-prod']/a)[" + i + "]"))
					.getAttribute("href");
			String courseTotalPrice = driver.findElement(By.xpath("(//div[@class='card-prod-price'])[" + i + "]"))
					.getText();
			if (!courseTotalPrice.contains("cursos em até 12x de")) {
				Courses.addCourse(new Course(courseName, detailsLink,
						new BigDecimal(courseTotalPrice.replace("R$ ", "").replace(".", "").replace(",", "."))));
			} else {
				Courses.addCourse(new Course(courseName, detailsLink, null));
			}
		}
	}

	@Entao("^eu valido se o valor do curso na pagina de listagem e igual ao valor na pagina de detalhes$")
	public void euValidoSeOValorDoCursoNaPaginaDeListagemEIgualAoValorNaPaginaDeDetalhes() throws Throwable {
		for (Course course : Courses.getCourse()) {
			if (course.getCourseTotalPrice() != null) {
				driver.get(course.getDetailsLink());
				BigDecimal detailsPagePrice = new BigDecimal(
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='value']")))
								.getText().replace("R$ ", "").replace(".", "").replace(",", "."));
				assertTrue(
						"Valor total na pagina de listagem: R$ " + course.getCourseTotalPrice()
								+ "\nValor total na pagina de detalhes: R$ " + detailsPagePrice,
						course.getCourseTotalPrice().equals(detailsPagePrice));
			}
		}
	}

	@E("^verifico que o total do valor parcelado do curso corresponde ao valor total$")
	public void verificoQueOTotalDoValorParceladoDoCursoCorrespondeAoValorTotal() throws Throwable {
		for (Course course : Courses.getCourse()) {
			if (course.getCourseTotalPrice() == null) {
				driver.get(course.getDetailsLink());
				BigDecimal detailsPageTotalPrice = new BigDecimal(
						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='value']")))
								.getText().replace("R$ ", "").replace(".", "").replace(",", "."));
				BigDecimal detailsPageInstallmentsPrice = new BigDecimal(
						driver.findElement(By.xpath("//div[@class='cur-details-shopping-installments']")).getText()
								.replace("ou 12x de R$ ", "").replace(".", "").replace(",", "."));
				assertTrue(
						"Curso: " + course.getCourseName() + "\nValor parcelado na pagina de listagem: 12 x R$ "
								+ detailsPageInstallmentsPrice + " = R$ "
								+ detailsPageInstallmentsPrice.multiply(new BigDecimal(12)).setScale(0, RoundingMode.UP)
								+ "\nValor total na pagina de detalhes: R$ " + detailsPageTotalPrice,
						detailsPageInstallmentsPrice.multiply(new BigDecimal(12)).setScale(0, RoundingMode.UP)
								.equals(detailsPageTotalPrice));
			}
		}
	}

	@E("^que a quantidade de cursos exibidos na pagina de listagem e igual a quantidade de cursos na pagina de detalhes$")
	public void queAQuantidadeDeCursosExibidosNaPaginaDeListagemEIgualAQuantidadeDeCursosNaPaginaDeDetalhes()
			throws Throwable {
		int totalCoursesDetailsPage = Courses.getCourse().stream()
				.filter(x -> !x.getCourseName().contains("Assinatura")).collect(Collectors.toList()).size();
		assertTrue(
				"Quantidade cursos disponiveis na listagem:" + totalCoursesAvailableInListPage
						+ "\nQuantidade de cursos na pagina de detalhes: " + totalCoursesDetailsPage,
				totalCoursesAvailableInListPage == totalCoursesDetailsPage);
	}

	@Quando("^eu realizar a pesquisa na barra de busca$")
	public void euRealizarAPesquisaNaBarraDeBusca(DataTable dataTable) throws Throwable {
		for (Map<String, String> map : dataTable.asMaps()) {
			driver.findElement(By.name("q")).sendKeys(map.get("pesquisa") + Keys.ENTER);
		}
	}

	@E("^selecionar o filtro '(.*?)'$")
	public void selecionarOFiltro(String filter) throws Throwable {
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'" + filter + "')]")))
				.click();
		Thread.sleep(2000);
	}

	@Entao("^os resultados serao exibidos de acordo com o filtro selecionado$")
	public void osResultadosSeraoExibidosDeAcordoComOFiltroSelecionado() throws Throwable {
		List<String> filterResults = wait
				.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h1[@class='card-prod-title']/a")))
				.stream().map(x -> x.getText()).collect(Collectors.toList());
		for (String filterResult : filterResults) {
			assertTrue("Resultado " + filterResult + " não é plano de assinatura.",
					filterResult.toLowerCase().contains("assinatura"));
		}
	}

	@Entao("^valido que todos os resultados retornados pela API sao exibidos$")
	public void validoQueTodosOsResultadosRetornadosPelaAPISaoExibidos() throws Throwable {
		List<JSONObject> apiResults = listAPIResults();
		assertTrue(
				"Total de resultados na pagina: " + webPageResults + "\nTotal de resultados API: " + apiResults.size(),
				apiResults.size() == webPageResults);
	}

	@E("^listar os resultados exibidos na pagina$")
	public void listarOsResultadosExibidosNaPagina() throws Throwable {
		boolean moreResults = true;
		while (moreResults) {
			Thread.sleep(2000);
			((JavascriptExecutor) driver)
					.executeScript("document.querySelector('button[class=button-more]').scrollIntoView(false);", "");
			WebElement buttonMoreResults = wait
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class='button-more']")));
			try {
				buttonMoreResults.click();
			} catch (ElementNotInteractableException e) {
				moreResults = false;
			}
		}
		webPageResults = (Long) ((JavascriptExecutor) driver)
				.executeScript("return document.querySelectorAll('section[class=card-prod]').length", "");
	}

	@E("^exibir os detalhes do pacote$")
	public void exibirOsDetalhesDoPacote() throws Throwable {
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Detalhes')]"))).click();
	}

	@Entao("^valido o desconto no pagamento a vista$")
	public void validoODescontoNoPagamentoAVista() throws Throwable {
		BigDecimal valorTotal = new BigDecimal(
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='from']"))).getText()
						.trim().replaceAll("de R\\$ ", "").replace(".", "").replace(",", "."));
		BigDecimal valorComDesconto = new BigDecimal(driver.findElement(By.xpath("//div[@class='value']")).getText()
				.trim().replaceAll("por R\\$ ", "").replace(".", "").replace(",", "."));
		String porcentagemDesconto = driver.findElement(By.xpath("//div[@class='details']")).getText().trim();
		BigDecimal desconto = new BigDecimal(
				porcentagemDesconto.substring(porcentagemDesconto.indexOf("(") + 1, porcentagemDesconto.length() - 2));
		BigDecimal valorDescontoCalculado = valorTotal.multiply(desconto).multiply(new BigDecimal("0.01")).setScale(0,
				RoundingMode.UP);
		assertTrue(valorComDesconto.equals(valorTotal.subtract(valorDescontoCalculado)));
	}

	private List<JSONObject> listAPIResults() {
		List<JSONObject> apiResults = new ArrayList<>();
		boolean hasMore = true;
		int i = 1;
		while (hasMore) {
			JSONObject jsonResponse = new JSONObject(
					given().queryParams("q", "oab").queryParam("p", i).queryParam("tipo", "cursos").when()
							.get("https://www.estrategiaconcursos.com.br/pesquisa/main/json/").then().statusCode(200)
							.extract().body().asString());
			JSONArray responseJSON = jsonResponse.getJSONArray("result");
			responseJSON.forEach(x -> apiResults.add(new JSONObject(x.toString())));
			hasMore = jsonResponse.getBoolean("hasMore");
			i++;
		}
		return apiResults;
	}

}
