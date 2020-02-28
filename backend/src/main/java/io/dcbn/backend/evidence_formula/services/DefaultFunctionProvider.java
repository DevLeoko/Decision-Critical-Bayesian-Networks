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

    private Object distanceToNearest(List<Object> parameters) {
        Set<Vessel> allVessels = vesselCache.getAllVesselsInTimeSlice(currentTimeSlice);
        return distance(currentVessel, allVessels);
    }

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
            double currentDistance = vesselPoint.distance(entryVesselPoint);

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
