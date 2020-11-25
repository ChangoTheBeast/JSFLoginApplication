package com.sparta.reece.resources;


import com.sparta.reece.entities.UsersEntity;
import com.sparta.reece.services.UsersDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsersResource {

    @Inject
    UsersDAO usersDAO;

    @GET
    @Path("{id}")
    public UsersEntity getUserByID(@PathParam("id") int id) {
        return usersDAO.findUserById(id);
    }

    @GET
    @Path("/roles")
    public List<UsersEntity> getUsersByRole(@QueryParam("role") String role) {
        return usersDAO.getUsersByRole(role);
    }
}
