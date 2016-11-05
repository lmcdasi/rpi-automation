package com.home.automation.cloud.devices;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.home.automation.cloud.auth.AuthenticateDevice;
import com.home.automation.cloud.auth.AuthenticateState;
import com.home.automation.cloud.events.Request;
import com.home.automation.garage.DoorRelay;
import com.home.automation.garage.DoorState;

public class DeviceTurnOn extends ServerResource {
	private static Logger logger = Logger.getLogger(DeviceTurnOn.class.getCanonicalName());

	private DoorRelay garageDoor = null;

	@Post("application/json")
	public void doPost(Representation entity) {
		GsonRepresentation<Request> represent = new GsonRepresentation<Request>(entity, Request.class);
		Request req;
		try {
			req = (Request) represent.getObject();

			AuthenticateDevice authDev = new AuthenticateDevice(req.getAccessToken());
			if (authDev.authDevice() == AuthenticateState.SUCCESS) {
/*				garageDoor = DoorRelay.getDoorRelayInstance();

				switch (garageDoor.getDoorState()) {
				case OPENED:
					logger.warn("Door already opened");
					break;
				default:
					DoorState executionState = garageDoor.activateRelay();
					logger.debug("ExecutionState: " + executionState);
				}*/

				this.getResponse().setStatus(Status.SUCCESS_OK);
				logger.info("Success - door activated - acess token:" + req.getAccessToken());
			}
		} catch (IOException ioex) {
			this.getResponse().setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED);
			logger.error("IOException", ioex);
		} /*catch (InterruptedException iex) {
			this.getResponse().setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED);
			logger.error("IOException", iex);
		} catch (NoSuchFieldException nsfex) {
			logger.error("NoSuchFieldException", nsfex);
		} catch (SecurityException sex) {
			logger.error("SecurityException", sex);
		} catch (IllegalArgumentException iaex) {
			logger.error("IllegalArgumentException", iaex);
		} catch (IllegalAccessException iaex) {
			logger.error("IllegalAccessException", iaex);
		}*/
	}

	@Get
	public void doGet(Representation entity) {
		this.getResponse().setStatus(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
}
