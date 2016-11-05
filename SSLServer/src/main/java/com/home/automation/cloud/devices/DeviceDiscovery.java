package com.home.automation.cloud.devices;

import java.io.IOException;

import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.home.automation.cloud.events.Request;

public class DeviceDiscovery extends ServerResource {
//https://www.javacodegeeks.com/2013/09/restlet-framework-hello-world-example.html
    @Post("application/json")
    public Representation doPost(Representation entity) throws IOException {
    	GsonRepresentation<Request> represent = new GsonRepresentation<Request>(entity, Request.class);
    	Request req	= (Request) represent.getObject();
    	
		return entity;
    }
}
