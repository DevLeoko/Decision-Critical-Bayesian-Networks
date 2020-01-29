package io.dcbn.backend.evidenceFormula.services;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import de.fraunhofer.iosb.iad.maritime.datamodel.VesselType;
import io.dcbn.backend.core.AoiCache;
import io.dcbn.backend.core.VesselCache;
import io.dcbn.backend.evidenceFormula.services.exceptions.SymbolNotFoundException;
import io.dcbn.backend.evidenceFormula.services.visitors.FunctionWrapper;

import java.util.*;

public class DefaultFunctionProvider extends FunctionProvider {

    private VesselCache vesselCache;
    private AoiCache aoiCache;

    private Object inArea(List<Object> parameters, Set<Vessel> ignored, Set<AreaOfInterest> correlatedAois, int timeSlice) {
        String aoiName = (String) parameters.get(0);
        if (aoiCache.getAoi(aoiName) == null) {
            throw new SymbolNotFoundException(aoiName);
        }
        AreaOfInterest aoi = aoiCache.getAoi(aoiName);
        correlatedAois.add(aoi);

        Point vesselPoint = createPoint(currentVessel);

        return aoi.getGeometry().contains(vesselPoint);
    }

    private Object distanceToNearest(List<Object> parameters, Set<Vessel> correlatedVessel, Set<AreaOfInterest> correlatedAois, int timeSlice) {
        Set<Vessel> allVessels = vesselCache.getAllVesselsInTimeSlice(timeSlice);
        return distance(currentVessel, allVessels, correlatedVessel);
    }

    public Object distanceToNearestType(List<Object> parameters, Set<Vessel> correlatedVessel, Set<AreaOfInterest> correlatedAois, int timeSlice) {
        Set<Vessel> allVesselsByType = vesselCache.getAllVesselsInTimeSliceByType(timeSlice, (VesselType) parameters.get(0));
        return distance(currentVessel, allVesselsByType, correlatedVessel);
    }

    private Point createPoint(Vessel vessel) {
        Coordinate[] coords = new Coordinate[1];
        coords[0] = new Coordinate(vessel.getLongitude(), vessel.getLatitude());
        CoordinateSequence coordsSequence = new CoordinateArraySequence(coords);
        return new Point(coordsSequence, new GeometryFactory());
    }

    private double distance(Vessel vessel, Set<Vessel> vesselSet, Set<Vessel> correlatedVessel) {
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
            correlatedVessel.add(currentCorrelatedVessel);
        }
        return distance;
    }



    public DefaultFunctionProvider(VesselCache vesselCache, AoiCache aoiCache) {
        this.vesselCache = vesselCache;
        this.aoiCache = aoiCache;

        Map<String, FunctionWrapper> functions = new HashMap<>();
        functions.put("inArea", new FunctionWrapper(Collections.singletonList(String.class), this::inArea));
        functions.put("distanceToNearest", new FunctionWrapper(Collections.emptyList(), this::distanceToNearest));
        functions.put("distanceToNearestType", new FunctionWrapper(Collections.singletonList(VesselType.class), this::distanceToNearestType));
        super.functions = functions;
    }
}
