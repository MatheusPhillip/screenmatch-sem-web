package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.service.ConsumoAPI;

import java.util.Scanner;

public class Principal {

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apiKey=67abc709";

    private Scanner leitor = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();


    public void exibeMenu(){
        System.out.println("Digite o nome da série: ");
        var nomeSerie = leitor.nextLine();

        consumoAPI = new ConsumoAPI();
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);

    }
}
