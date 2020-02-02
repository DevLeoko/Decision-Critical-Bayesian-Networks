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

    private Object inArea(List<Object> parameters) {
        String aoiName = (String) parameters.get(0);
        if (aoiCache.getAoi(aoiName) == null) {
            throw new SymbolNotFoundException(aoiName);
        }
        AreaOfInterest aoi = aoiCache.getAoi(aoiName);
        correlatedAois.add(aoi);

        Point vesselPoint = createPoint(currentVessel);

        return aoi.getGeometry().contains(vesselPoint);
    }

    private Object distanceToNearest(List<Object> parameters) {
        Set<Vessel> allVessels = vesselCache.getAllVesselsInTimeSlice(currentTimeSlice);
        return distance(currentVessel, allVessels);
    }

    private Object distanceToNearestType(List<Object> parameters) {
        Set<Vessel> allVesselsByType = vesselCache.getAllVesselsInTimeSliceByType(currentTimeSlice, (VesselType) parameters.get(0));
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
        functions.put("distanceToNearestType", new FunctionWrapper(Collections.singletonList(VesselType.class), this::distanceToNearestType));
        super.functions = functions;
    }

    private Object inArea(List<Object> parameters, Set<Vessel> ignored, Set<AreaOfInterest> correlatedAois, int timeSlice) {
        correlatedAois.add(new AreaOfInterest("TEST_AREA", null));
        return "TEST_AREA".equals(parameters.get(0));
    }

}
