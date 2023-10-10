package com.esed.pagopa.pdf.webservice.resources;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.esed.pagopa.pdf.SalvaPDF;
import com.esed.pagopa.pdf.SalvaPDFBolzano;
import com.esed.pagopa.pdf.SalvaPDFRegMarche;
import com.esed.pagopa.pdf.ValidazioneException;
import com.esed.pagopa.pdf.config.PropKeys;
import com.esed.pagopa.pdf.webservice.applications.ApplicationV1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seda.commons.properties.tree.PropertiesTree;
import com.seda.payer.commons.geos.Flusso;
import com.seda.payer.commons.inviaAvvisiForGeos.FlussoMassivo;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("resource")
public class Resource {

	private static final Logger logger = Logger.getLogger(Resource.class);
	
	@POST
	@Path("/pdf")
	public Response getPdf(Flusso flusso) {
		logger.info("getPdf INIT!");
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String flussoAsString = objectMapper.writeValueAsString(flusso);
			logger.debug(flussoAsString);
		} catch (JsonProcessingException e) {
			
			logger.error("", e);
		}
		
		byte[] array = null;
		String tipoStampa = "";

			if(ApplicationV1.getPropertiesTree().getProperty(PropKeys.stampaJppa.format(flusso.CuteCute)).equals("Y")) {
				tipoStampa = "jppa";
		}
		
		try {
			SalvaPDF salvaPDF = new SalvaPDF(ApplicationV1.getPropertiesTree());
			
			if(tipoStampa.equals("jppa")) {
				System.out.println("Stampa tipo jppa dentro webservice");
				array = Base64.getDecoder()
						.decode(salvaPDF.SalvaFile(flusso,tipoStampa));
			}else {
			array = salvaPDF.SalvaFile(flusso,tipoStampa);
			}
			
			
		} catch (IOException | ValidazioneException e) {
			logger.error(null, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}catch (Throwable ex) {
			// TODO: handle exception
			ex.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();			
		}
		
		ResponseBuilder response = Response.status(Status.OK).entity(array);
		return response.build();
	}
	
	@POST
	@Path("/pdfs")
	public Response getPdfs(FlussoMassivo flusso) {
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String flussoAsString = objectMapper.writeValueAsString(flusso);
			logger.debug(flussoAsString);
		} catch (JsonProcessingException e) {
			
			logger.error("", e);
		}
		
		UUID uudi = UUID.randomUUID();
		logger.info("UUID: " + uudi.toString());
		
		Thread thread = new Thread(() -> {
			SalvaPDF salvaPDF = new SalvaPDF(ApplicationV1.getPropertiesTree());
			salvaPDF.SalvaFileMassivo(uudi, flusso.flussoList, flusso.path);
		});
		thread.start();
		
		return Response.status(Status.OK).entity(uudi.toString()).build();
	}
	
	//inizio LP PG210070
	@POST
	@Path("/pdfBolzano")
	public Response getPdfBolzano(Flusso flusso) throws IOException, ValidazioneException {
		
		byte[] array = null;
		String tipoStampa = "";
		
			if(ApplicationV1.getPropertiesTree().getProperty(PropKeys.stampaJppa.format("000P6")).equals("Y")) {
				tipoStampa = "jppa";
			}

		try {
			if(tipoStampa.equals("jppa") || tipoStampa.equals("jppade")) {
				System.out.println("Dentro Webservice - Stampa di tipo jppa");
				array = Base64.getDecoder().decode(SalvaPDFBolzano.SalvaFile(flusso,tipoStampa,ApplicationV1.getPropertiesTree()));
			}else {
				array = SalvaPDFBolzano.SalvaFile(flusso,tipoStampa,ApplicationV1.getPropertiesTree());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ResponseBuilder response = Response.status(Status.OK).entity(array);
		return response.build();
	}
	//fine LP PG210070
	//inizio SB PG210170
		@POST
		@Path("/pdfRegMarche")
		public Response getPdfRegMarche(Flusso flusso) {
			
			byte[] array = null;
			
			try {
				array = SalvaPDFRegMarche.SalvaFile(flusso);
			} catch (IOException | ValidazioneException e) {
				logger.error(null, e);
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			}
			
			ResponseBuilder response = Response.status(Status.OK).entity(array);
			return response.build();
		}
		//fine SB PG210170
		
		
		@POST
		@Path("/pdfsRegMarche")
		public Response getPdfsRegMarche(FlussoMassivo flusso) {
			
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				String flussoAsString = objectMapper.writeValueAsString(flusso);
				logger.debug(flussoAsString);
			} catch (JsonProcessingException e) {
				
				logger.error("", e);
			}
			
			UUID uudi = UUID.randomUUID();
			logger.info("UUID: " + uudi.toString());
			
			Thread thread = new Thread(() -> {
				SalvaPDFRegMarche salvaPDFRegMarche = new SalvaPDFRegMarche(ApplicationV1.getPropertiesTree());
				salvaPDFRegMarche.salvaFileMassivo(uudi, flusso.flussoList, flusso.path);
			});
			thread.start();
			
			return Response.status(Status.OK).entity(uudi.toString()).build();
		}
}
