package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoApi;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner scanner = new Scanner(System.in);
    private final ConsumoApi api = new ConsumoApi();
    private final ConverteDados conversor = new ConverteDados();
    private final String ENDERECO ="https://parallelum.com.br/fipe/api/v1/";

    public void exibirMenu() {

        var menuApresentacao =
                """
                ### CONSULTA DE TABELA FIPE ###
                ######## OPÇÕES ########
                1 - Consultar carros
                2 - Consultar caminhões
                3 - Consultar motos
                
                Informe a opção desejada:""";
        System.out.println(menuApresentacao);
        int opcao = scanner.nextInt();

        String link = "";

        if(opcao == 1) {
            link = ENDERECO + "carros/marcas";

        } else if (opcao == 2) {
            link = ENDERECO + "caminhoes/marcas";

        } else if (opcao == 3) {
            link = ENDERECO + "motos/marcas";

        } else {
            System.out.println("Programa encerrado!");
        }

        var json = api.obterDados(link);
        var listaDeMarcas = conversor.obterLista(json, Dados.class);
        listaDeMarcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca que deseja consultar: ");
        String codigoMarca = scanner.next();

        link = link + "/" + codigoMarca + "/modelos";
        json = api.obterDados(link);
        var listaDeModelos = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca:");
        listaDeModelos.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nInforme o nome do modelo que deseja consultar: ");
        String nomeModelo = scanner.next();

        List<Dados> modelosFiltrados = listaDeModelos.modelos().stream()
                .filter(m-> m.nome().toLowerCase().contains(nomeModelo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o código do modelo que deseja consultar: ");
        var codigoModelo = scanner.next();

        link = link + "/" + codigoModelo + "/anos";
        json = api.obterDados(link);

        List<Dados> listaDeAnos = conversor.obterLista(json, Dados.class);
        List<Veiculo> listaVeiculos = new ArrayList<>();

        for (int i = 0; i < listaDeAnos.size(); i++) {
            var enderecoAnos = link + "/" + listaDeAnos.get(i).codigo();
            json = api.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            listaVeiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliações por ano");
        listaVeiculos.forEach(System.out::println);

    }
}
