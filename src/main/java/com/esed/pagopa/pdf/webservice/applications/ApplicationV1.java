package com.esed.pagopa.pdf.webservice.applications;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.log4j.Logger;

import com.esed.pagopa.pdf.webservice.config.PrintStrings;
import com.esed.pagopa.pdf.webservice.resources.Resource;
import com.seda.commons.properties.PropertiesLoader;
import com.seda.compatibility.SystemVariable;
import com.seda.commons.properties.tree.PropertiesNodeException;
import com.seda.commons.properties.tree.PropertiesTree;

@ApplicationPath("/v1") 
public class ApplicationV1 extends Application {

	private static Logger logger = Logger.getLogger(ApplicationV1.class);
	private static final PropertiesTree propertiesTree;
	
	static {
		System.out.println("init!");
		SystemVariable sv = new SystemVariable();
		
		try {
			String rootPath = sv.getSystemVariableValue(PrintStrings.ROOT.format());
			
			if (rootPath == null || !new File(rootPath).exists()) {
				throw new RuntimeException("File di configurazione non impostato");
			}else
				System.out.println("rootPath = " + rootPath);
			
			try {
				propertiesTree = new PropertiesTree(PropertiesLoader.load(rootPath));
			} catch (IOException | PropertiesNodeException e) {
				e.printStackTrace();
				throw new RuntimeException("Impossibile caricare il file di configurazione", e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Impossibile caricare il file di configurazione", e);
			// TODO: handle exception
		}
		
	}
	
	public static PropertiesTree getPropertiesTree() {
		return propertiesTree;
	}
	
	private Set<Object> singleton = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	
	public ApplicationV1() {
		classes.add(Resource.class);
		logger.info("Pronto!");
	}

	@Override
	public Set<Class<?>> getClasses() {
		return this.classes;
	}

	@Override
	public Set<Object> getSingletons() {
		return this.singleton;
	}
	
}
