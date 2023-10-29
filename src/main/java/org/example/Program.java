package org.example;

import org.example.lib.Box;

import java.util.Arrays;
import java.util.List;

public class Program {
    private final PokemonService _pokemonService = new PokemonService();
    public void execute(){
        loadPokemons(Arrays.asList("bulbasaur", "charmander", "squirtle", "carterpie", "weedle", "pidgey", "rattata",
                "spearow", "ekans", "pikachu", "sandshrew", "clfairy", "ninetales", "jigglypuff"));
    }

    private void loadPokemons(List<String> pokemonsToShow){
        Box.from(pokemonsToShow)
                .eachFinalAsync((String id) ->
                        Box.from(id)
                            .then(_pokemonService::pokemonFromQuery, this::handlePokemonFail)
                            .thenFinal(this::updatePokemonList),
                System.out::println);
    }

    private void fetchPoke(String id){
        Box.from(id)
            .then(_pokemonService::pokemonFromQuery, this::handlePokemonFail)
            .thenFinal(this::updatePokemonList);
    }

    // Draw a default card or something ...
    private void handlePokemonFail(Exception e){ System.out.println(e.getMessage()); }

    private void handlePokemonFail(Exception e)
            -> System.out.println(e.getMessage());

    private void updatePokemonList(Pokemon pokemon) {
        // Update UI
        System.out.println("Found " + pokemon.name());
    }
}

record Pokemon(String name) {}

class PokemonService{
    public PokemonService(){ }

    public Pokemon pokemonFromQuery(String id) throws Exception
    {
        if(id.equals("ekans")){
            throw new Exception(String.format("Fail while loading \"%s\"", id));
        }

        return new Pokemon(id);
    }
}

