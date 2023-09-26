package com.esed.pagopa.pdf.webservice;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seda.payer.commons.geos.Flusso;

public class Test {

	public static void main(String[] args) {
		
		try {
			Client client = ClientBuilder.newClient();
	
			String uri = "http://localhost:8081/PagoPAPdfService/v1/resource//pdf";
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			String json = Files.
					readAllLines(Paths.get("C:\\work\\micci_pagonet\\git\\com.esed.pagopa.pdf\\src\\main\\java\\com\\esed\\pagopa\\pdf\\test\\File512.json"))
					.stream().collect(Collectors.joining());
			
			Flusso flusso = objectMapper.readValue(json, Flusso.class);
			
			Response response = client.target(uri)
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(flusso, MediaType.APPLICATION_JSON));
			
			System.out.println(response.getStatus());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
