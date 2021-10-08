/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import commonTasks.dto.FlagDTO;
import java.util.List;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.service.impl.FlagService;

/**
 *
 * @author koben
 */
@Path("v1/flag")
@Produces("application/json")
@Consumes("application/json")
public class FlagRessource {

    @EJB
    private FlagService flagService;

    @GET
    @Path("list")
    public Response getList() {
        List<FlagDTO> datas = flagService.listFlags();
        JSONObject json = new JSONObject();
        json.put("data", new JSONArray(datas)).put("total", datas.size());
        return Response.ok().entity(json.toString()).build();
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") String id) {
        flagService.upadte(id);
        return Response.ok().build();
    }
}
