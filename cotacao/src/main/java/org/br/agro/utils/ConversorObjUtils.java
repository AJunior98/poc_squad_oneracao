package org.br.agro.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
public class ConversorObjUtils {

    public static Object jsonToObject(String json, Class classe) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).readValue(json, classe);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String getJsonCotacaoInfo(){
        return buscarArquivo("src/test/resources/", "cotacaoInfo.json");
    }

    private static String  buscarArquivo(String xPath, String nomeArquivo){
        Path path  =  Paths.get(xPath).resolve(nomeArquivo);
        try {
            return new String(Files.readAllBytes(path), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
