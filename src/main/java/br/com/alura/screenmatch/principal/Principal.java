package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apiKey=67abc709";

    private Scanner leitor = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();


    public void exibeMenu() {
        System.out.println("Digite o nome da série: ");
        var nomeSerie = leitor.nextLine();


        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int season = 1; season <= dadosSerie.totalTemporadas(); season++) {
            var jsonSeason = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + season + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(jsonSeason, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        temporadas.forEach(System.out::println);

        for (int i = 0; i < dadosSerie.totalTemporadas(); i++) {
            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            for (DadosEpisodio dadosEpisodio : episodiosTemporada) {
                System.out.println(dadosEpisodio.titulo());
            }
        }

        temporadas.forEach(t -> t.episodios()
                .forEach(e -> System.out.println(e.titulo())));


        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\n\n Top 10 episódios:\n");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(10)
                .map(e -> e.titulo().toUpperCase())
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Informe um trecho do título do episódio: ");
        var trechoDoTitulo = leitor.nextLine();

        Optional<Episodio> episodioProcurado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoDoTitulo.toUpperCase()))
                .findFirst();

        if(episodioProcurado.isPresent()){
            System.out.println("Episódio encontrado!");
            System.out.println("Temporada: " + episodioProcurado.get().getTemporada());
            System.out.println("Titulo completo: " + episodioProcurado.get().getTitulo());
        }else{
            System.out.println("Episódio não encontrado!");
        }

        System.out.println("A partir de que ano você deseja ver os episódios? ");
        var ano = leitor.nextInt();
        leitor.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");


        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " Episódio: " + e.getTitulo() +
                                " Data lançamento: " + e.getDataLancamento().format(formatador)
                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage());
        System.out.println("Nota do melhor episódio: " + est.getMax());
        System.out.println("Nota do pior episódio: " + est.getMin());
        System.out.println("Quantidade de episódios: " + est.getCount());
    }
}

