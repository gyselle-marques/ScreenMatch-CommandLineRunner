package br.com.gyselle.screenmatch.main;

import br.com.gyselle.screenmatch.model.*;
import br.com.gyselle.screenmatch.repository.SerieRepository;
import br.com.gyselle.screenmatch.service.ConsumoApi;
import br.com.gyselle.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=971c53ec";
    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repository;

    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Main(SerieRepository repository) {

        this.repository = repository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    ### SCREEN MATCH ###
                    
                    1 - Buscar Séries
                    2 - Buscar Episódios
                    3 - Listar Séries Buscadas
                    4 - Buscar Séries por Título
                    5 - Buscar Séries por Atores
                    6 - Buscar Séries por Gênero
                    7 - Top 5 Séries
                    8 - Filtrar Séries
                    9 - Buscar Episódios por Trecho
                    10 - Buscar Episódios por Data de Lançamento
                    11 - Top 5 Episódios por Série
                    
                    0 - Sair
                    
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtores();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    buscarTop5Series();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodiosPorTrecho();
                    break;
                case 10:
                    buscarEpisodiosPorDataLancamento();
                    break;
                case 11:
                    topEpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Digite uma opção válida!");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome de uma série para busca: ");
        var nomeSerie = leitura.nextLine();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série para busca de episódios: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas() {
        series = repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo() {
        System.out.println("Digite o título da série para busca: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Série encontrada: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriesPorAtores() {
        System.out.println("Digite um nome para busca: ");
        String nomeAtor = leitura.nextLine();
        System.out.println("Digite a avaliação mínima desejada: ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries encontradas com " + nomeAtor + ":");
        seriesEncontradas.forEach(s ->
                System.out.println("Título: " + s.getTitulo() + ", Avaliação: " + s.getAvaliacao() + ", Gênero: " + s.getGenero()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Digite a categoria/gênero da série: ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("Séries encontradas do gênero " + nomeGenero + ":");
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarTop5Series() {
        List<Serie> top5Series = repository.findTop5ByOrderByAvaliacaoDesc();
        top5Series.forEach(s ->
                System.out.println("Título: " + s.getTitulo() + ", Avaliação: " + s.getAvaliacao() + ", Gênero: " + s.getGenero()));
    }

    private void filtrarSeriesPorTemporadaEAvaliacao(){
        System.out.println("Filtrar séries de até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliações a partir de qual nota? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repository.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - Temporadas: " + s.getTotalTemporadas() + "  - Avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodiosPorTrecho() {
        System.out.println("Digite o nome do episódio para busca: ");
        String trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosFiltrados = repository.episodiosPorTrecho(trechoEpisodio);
        episodiosFiltrados.forEach(e ->
                System.out.println("Série: " + e.getSerie().getTitulo() +
                        ", Temporada: " + e.getTemporada() +
                        ", Episódio: " + e.getNumeroEpisodio() +
                        ", Título: " + e.getTitulo() +
                        ", Avaliação: " + e.getAvaliacao() +
                        ", Data de Lançamento: " + e.getDataLancamento()));
    }

    private void buscarEpisodiosPorDataLancamento() {
        buscarSeriesPorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano de lançamento: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repository.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

    private void topEpisodiosPorSerie() {
        buscarSeriesPorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repository.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.println("Série: " + e.getSerie().getTitulo() +
                            ", Temporada: " + e.getTemporada() +
                            ", Episódio: " + e.getNumeroEpisodio() +
                            ", Título: " + e.getTitulo() +
                            ", Avaliação: " + e.getAvaliacao()));
        }
    }
}