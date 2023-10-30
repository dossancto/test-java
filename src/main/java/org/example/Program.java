package org.example;

import org.example.lib.Box;

import java.util.Arrays;
import java.util.List;

public class Program {
    public void onCreate()
    {
        loadPokemons();
        // loadViews();
        // ....
    }

    private void loadPokemons()
    {
        new ListPokemonUseCase().execute(
                Arrays.asList("bulbasaur", "charmander", "squirtle", "carterpie", "weedle", "pidgey", "rattata",
                        "spearow", "ekans", "pikachu", "sandshrew", "clfairy", "ninetales", "jigglypuff"),

                this::updatePokemonList,
                this::handlePokemonFail);
    }

    private void handlePokemonFail(Exception e) {
        Box.from(new Pokemon("Empty"))
            .then(p -> new PokemonCard(p, "gray"))
            .thenFinal(this::renderCard);
    }

    private void updatePokemonList(Pokemon pokemon)
    {
        Box.from(pokemon)
                .then(this::pokemonToCard)
                .thenFinal(this::renderCard);
    }

    private void renderCard(PokemonCard card){
        System.out.printf("%s => %s%n", card.pokemon().name(), card.color());
    }

    private PokemonCard pokemonToCard(Pokemon pokemon)
    {
        return new PokemonCard(pokemon, "red");
    }
}

class ListPokemonUseCase {
    private final PokemonService _pokemonService = new PokemonService();

    public void execute(List<String> pokemons, Box.RunBoxWithP<Pokemon> newPokemon, Box.RunBoxWithP<Exception> pokemonfetchFail) {
        for (var pokemon : pokemons){
            Box.from(pokemon)
                    .then(_pokemonService::pokemonFromQuery, pokemonfetchFail)
                    .thenFinal(newPokemon);
        }
    }
}
record Pokemon(String name) {}
record PokemonCard(Pokemon pokemon, String color) {}

class PokemonService{
    public Pokemon pokemonFromQuery(String id) throws Exception
    {
        if(id.equals("ekans")){
            throw new Exception(String.format("Fail while loading \"%s\"", id));
        }

        return new Pokemon(id);
    }
}