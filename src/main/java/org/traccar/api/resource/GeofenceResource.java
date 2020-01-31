/*
 * Copyright 2016 - 2017 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.api.resource;

import org.traccar.Context;
import org.traccar.api.ExtendedObjectResource;
import org.traccar.model.Geofence;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("geofences")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeofenceResource extends ExtendedObjectResource<Geofence> {

    public GeofenceResource() {
        super(Geofence.class);
    }

    @POST
    @Override
    public Response add(Geofence entity) throws SQLException {
        try {
            return super.add(entity);
        } finally {
            Context.getGeofenceManager().notifyUpdate(entity);
        }
    }

    @Path("{id}")
    @PUT
    @Override
    public Response update(Geofence entity) throws SQLException {
        try {
            return super.update(entity);
        } finally {
            Context.getGeofenceManager().notifyUpdate(entity);
        }
    }

    @Path("{id}")
    @DELETE
    @Override
    public Response remove(@PathParam("id") long id) throws SQLException {
        Context.getGeofenceManager().notifyRemove(id);
        return super.remove(id);
    }
}
