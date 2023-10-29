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

    private void handlePokemonFail(Exception e){ System.out.println(e.getMessage()); }

    private void updatePokemonList(Pokemon pokemon)
    {
        // Update UI
        System.out.println("Fetch " + pokemon.name());
    }
}

class ListPokemonUseCase {
    private final PokemonService _pokemonService = new PokemonService();

    public void execute(List<String> pokemons, Box.RunBoxWithP<Pokemon> newPokemon, Box.RunBoxWithP<Exception> pokemonfetchFail) {
        Box.from(pokemons)
            .eachFinalAsync((String id) -> Box.from(id)
                                                .then(_pokemonService::pokemonFromQuery, pokemonfetchFail)
                                                .thenFinal(newPokemon));
    }
}
record Pokemon(String name) {}

class PokemonService{
    public Pokemon pokemonFromQuery(String id) throws Exception
    {
        if(id.equals("ekans")){
            throw new Exception(String.format("Fail while loading \"%s\"", id));
        }

        return new Pokemon(id);
    }
}

