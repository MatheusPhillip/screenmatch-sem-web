package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoAPI consumoAPI = new ConsumoAPI();
		var jsonSerie = consumoAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apiKey=67abc709");
		System.out.println(jsonSerie);
		//json = consumoAPI.obterDados("https://coffee.alexflipnote.dev/random.json");
		//System.out.println(json);

		ConverteDados conversor = new ConverteDados();
		DadosSerie dadosSerie = conversor.obterDados(jsonSerie, DadosSerie.class);
		System.out.println(dadosSerie);

		var jsonEpisode = consumoAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&Season=1&Episode=1&apiKey=67abc709");
		System.out.println(jsonEpisode);

		DadosEpisodio dadosEpisodio = conversor.obterDados(jsonEpisode, DadosEpisodio.class);
		System.out.println(dadosEpisodio);


		DadosTemporada dadosTemporada;
		List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
			var jsonSeason = consumoAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&Season=" + (i+1) + "&apiKey=67abc709");
			dadosTemporada = conversor.obterDados(jsonSeason, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

		temporadas.forEach(System.out::println);
	}
}
