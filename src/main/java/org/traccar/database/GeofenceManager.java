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
package org.traccar.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.traccar.Context;
import org.traccar.model.Device;
import org.traccar.model.Geofence;
import org.traccar.model.Position;

public class GeofenceManager extends ExtendedObjectManager<Geofence> {

    public GeofenceManager(DataManager dataManager) {
        super(dataManager, Geofence.class);
    }

    private List<Long> getGeofenceUsers(long geofenceId) {
        List<Long> result = new ArrayList<>();
        for (Map.Entry<Long, Set<Long>> entry : userItems.entrySet()) {
            if (entry.getValue().contains(geofenceId)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public void notifyUpdate(Geofence item) {
        for (long userId : getGeofenceUsers(item.getId())) {
            Context.getConnectionManager().updateGeofence(userId, item);
        }
    }

    public void notifyRemove(long itemId) {
        for (long userId : getGeofenceUsers(itemId)) {
            Context.getConnectionManager().deleteGeofence(userId, itemId);
        }
    }

    @Override
    public final void refreshExtendedPermissions() {
        super.refreshExtendedPermissions();
        recalculateDevicesGeofences();
    }

    public List<Long> getCurrentDeviceGeofences(Position position) {
        List<Long> result = new ArrayList<>();
        for (long geofenceId : getAllDeviceItems(position.getDeviceId())) {
            Geofence geofence = getById(geofenceId);
            if (geofence != null && geofence.getGeometry()
                    .containsPoint(position.getLatitude(), position.getLongitude())) {
                result.add(geofenceId);
            }
        }
        return result;
    }

    public void recalculateDevicesGeofences() {
        for (Device device : Context.getDeviceManager().getAllDevices()) {
            List<Long> deviceGeofenceIds = device.getGeofenceIds();
            if (deviceGeofenceIds == null) {
                deviceGeofenceIds = new ArrayList<>();
            } else {
                deviceGeofenceIds.clear();
            }
            Position lastPosition = Context.getIdentityManager().getLastPosition(device.getId());
            if (lastPosition != null && getAllDeviceItems(device.getId()) != null) {
                deviceGeofenceIds.addAll(getCurrentDeviceGeofences(lastPosition));
            }
            device.setGeofenceIds(deviceGeofenceIds);
        }
    }

}
