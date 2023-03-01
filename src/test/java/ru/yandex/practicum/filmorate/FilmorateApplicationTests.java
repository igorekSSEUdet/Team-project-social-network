package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmorateApplicationTests {
	private final HttpClient client = HttpClient.newHttpClient();
	private final URI uriUser = URI.create("http://localhost:8080/users");
	private final URI uriFilm = URI.create("http://localhost:8080/films");

	@Test
	void contextLoads() {
	}

	@Test
	void regUserWithEmptyLogin() throws IOException, InterruptedException {
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("{\n" +
				"  \"email\": \"yandex@mail.ru\",\n" +
				"  \"birthday\": \"1920-08-20\"\n" +
				"}");
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body).uri(uriUser).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(415, response.statusCode());
	}

	@Test
	void shouldNotRegWithWhitespaces() throws IOException, InterruptedException {
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("{\n" +
				"  \"login\": \"dolore ullamco\",\n" +
				"  \"email\": \"yandex@mail.ru\",\n" +
				"  \"birthday\": \"1920-08-20\"\n" +
				"}");
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body).uri(uriUser).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(415, response.statusCode());
	}

	@Test
	void shouldNotAddEmptyUserPost() throws IOException, InterruptedException {
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.noBody();
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body).uri(uriUser).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotAddUserWithWrongEmail() throws IOException, InterruptedException {
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("{\n" +
				"  \"login\": \"user\",\n" +
				"  \"email\": \"absolutelyNotWrong@\",\n" +
				"  \"birthday\": \"1920-08-20\"\n" +
				"}");
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body).uri(uriUser).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(415, response.statusCode());
	}

	@Test
	void shouldAddUserWithEmptyName() throws IOException, InterruptedException {
		String user = "{\"login\": \"dolore\",\"name\": \"Nick Name\",\"email\": " +
				"\"mail@mail.ru\",\"birthday\": \"1946-08-20\"}";
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(user);
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body)
						.header("Content-Type", "application/json").uri(uriUser).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
	}

	@Test
	void shouldNotAddUserWithBirthdayInTheFuture() throws IOException, InterruptedException {
		String user = "{\"login\": \"dolore\",\"name\": \"Nick Name\",\"email\": " +
				"\"mail@mail.ru\",\"birthday\": \"2300-08-20\"}";
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(user);
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body)
						.header("Content-Type", "application/json").uri(uriUser).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotAddFilmWithEmptyName() throws IOException, InterruptedException {
		String film = "{\n" +
				"  \"description\": \"adipisicing\",\n" +
				"  \"releaseDate\": \"1967-03-25\",\n" +
				"  \"duration\": 100\n" +
				"}";
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(film);
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body)
						.header("Content-Type", "application/json").uri(uriFilm).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotAddFilmWithLongDescription() throws IOException, InterruptedException {
		String film = "{\n" +
				"  \"name\": \"nisi eiusmod\",\n" +
				"  \"description\": \"adipisicingvkfvklxfjvklfvjklxfvjfklvjxfklvjfklvjfklvjkxfvjkxfvjkxfjvkxfjv" +
				"kfvjxfklvjxfklvjxfklvjxfklvjfklvjklxfvjfklvjklfvjklxfvjklxfjvklxfjvklxfjvkxfvjkxfjvkxfjvklxfjvkljfvlk" +
				"kvjxfklvjxfkvljxfklvjxklvjxlkvjxklvjxvjfkljvklxfjvklxfjvkxfvjxfkvjxfklvjklxfjklxfjxfkvjklxfj\",\n" +
				"  \"releaseDate\": \"1967-03-25\",\n" +
				"  \"duration\": 100\n" +
				"}";
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(film);
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body)
						.header("Content-Type", "application/json").uri(uriFilm).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldNotAddFilmBefore1895() throws IOException, InterruptedException {
		String film = "{\n" +
				"  \"name\": \"nisi eiusmod\",\n" +
				"  \"description\": \"adipisicing\",\n" +
				"  \"releaseDate\": \"1800-03-25\",\n" +
				"  \"duration\": 100\n" +
				"}";
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(film);
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body)
						.header("Content-Type", "application/json").uri(uriFilm).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(500, response.statusCode());
	}

	@Test
	void shouldNotAddFilmWithNegativeDuration() throws IOException, InterruptedException {
		String film = "{\n" +
				"  \"name\": \"nisi eiusmod\",\n" +
				"  \"description\": \"adipisicing\",\n" +
				"  \"releaseDate\": \"2000-03-25\",\n" +
				"  \"duration\": -100\n" +
				"}";
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(film);
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body)
						.header("Content-Type", "application/json").uri(uriFilm).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(400, response.statusCode());
	}

	@Test
	void shouldAddFilm() throws IOException, InterruptedException {
		String film = "{\n" +
				"  \"name\": \"nisi eiusmod\",\n" +
				"  \"description\": \"adipisicing\",\n" +
				"  \"releaseDate\": \"2000-03-25\",\n" +
				"  \"duration\": 100\n" +
				"}";
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(film);
		HttpResponse<String> response = client.send(HttpRequest.newBuilder().POST(body)
						.header("Content-Type", "application/json").uri(uriFilm).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, response.statusCode());
	}
}