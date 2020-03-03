package io.dcbn.backend.evidence_formula.services;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import de.fraunhofer.iosb.iad.maritime.datamodel.VesselType;
import io.dcbn.backend.core.AoiCache;
import io.dcbn.backend.core.VesselCache;
import io.dcbn.backend.evidence_formula.services.exceptions.SymbolNotFoundException;
import io.dcbn.backend.evidence_formula.services.visitors.FunctionWrapper;

import java.util.*;

public class DefaultFunctionProvider extends FunctionProvider {


    private VesselCache vesselCache;
    private AoiCache aoiCache;

    /**
     * Checks if a ship is in a given area
     *
     * @param parameters function parameters
     * @return true if ship is in given area, else false
     */
    private Object inArea(List<Object> parameters) {
        String aoiName = (String) parameters.get(0);
        AreaOfInterest aoi = aoiCache.getAoi(aoiName);
        if (aoi == null) {
            throw new SymbolNotFoundException(aoiName);
        }
        if (currentVessel.getLongitude() == null || currentVessel.getLatitude() == null) {
            throw new IllegalArgumentException("Longitude and latitude has to be set!");
        }

        correlatedAois.add(aoi.getName());

        Point vesselPoint = createPoint(currentVessel);

        return aoi.getGeometry().contains(vesselPoint);
    }

    /**
     * Returns the distance of the current ship and the nearest ship in meters
     *
     * @param parameters function parameters
     * @return distance in meters
     */
    private Object distanceToNearest(List<Object> parameters) {
        Set<Vessel> allVessels = vesselCache.getAllVesselsInTimeSlice(currentTimeSlice);
        return distance(currentVessel, allVessels);
    }

    /**
     * Returns the distance of the current ship and the nearest ship of a given type in meters
     *
     * @param parameters function parameters
     * @return distance in meters
     */
    private Object distanceToNearestType(List<Object> parameters) {
        VesselType type = VesselType.valueOf((String) parameters.get(0));
        if (type == null) {
            throw new IllegalArgumentException("VesselType has to be set!");
        }

        Set<Vessel> allVesselsByType = vesselCache.getAllVesselsInTimeSliceByType(currentTimeSlice, type);
        return distance(currentVessel, allVesselsByType);
    }

    private Point createPoint(Vessel vessel) {
        Coordinate[] coords = new Coordinate[1];
        coords[0] = new Coordinate(vessel.getLongitude(), vessel.getLatitude());
        CoordinateSequence coordsSequence = new CoordinateArraySequence(coords);
        return new Point(coordsSequence, new GeometryFactory());
    }

    private double distance(Vessel vessel, Set<Vessel> vesselSet) {
        Point vesselPoint = createPoint(vessel);
        double distance = Double.POSITIVE_INFINITY;
        Vessel currentCorrelatedVessel = null;
        for (Vessel entry : vesselSet) {
            if(vessel.getUuid().equals(entry.getUuid())) {
                continue;
            }
            Point entryVesselPoint = createPoint(entry);

            double currentDistance = coordinateDistanceToMeter(vessel.getLatitude(), vessel.getLongitude(), entry.getLatitude(), entry.getLongitude());

            if(distance > currentDistance) {
                currentCorrelatedVessel = entry;
                distance = currentDistance;
            }
        }
        if (currentCorrelatedVessel != null) {
            correlatedVessels.add(currentCorrelatedVessel);
        }
        return distance;
    }

    private double coordinateDistanceToMeter(double lat1, double lon1, double lat2, double lon2) {
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }

    public DefaultFunctionProvider(VesselCache vesselCache, AoiCache aoiCache) {
        this.vesselCache = vesselCache;
        this.aoiCache = aoiCache;

        Map<String, FunctionWrapper> functions = new HashMap<>();
        functions.put("inArea", new FunctionWrapper(Collections.singletonList(String.class), this::inArea));
        functions.put("distanceToNearest", new FunctionWrapper(Collections.emptyList(), this::distanceToNearest));
        functions.put("distanceToNearestType", new FunctionWrapper(Collections.singletonList(String.class), this::distanceToNearestType));
        super.functions = functions;
    }

}
