package io.dcbn.backend.core;

import com.vividsolutions.jts.geom.Geometry;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class AoiCacheTest {

    AoiCache cache;
    AreaOfInterest aoiOne;
    AreaOfInterest aoiTwo;

    @BeforeEach
    public void setUp() {
        cache = new AoiCache();
        aoiOne = new AreaOfInterest("aoi1", mock(Geometry.class));
        aoiTwo = new AreaOfInterest("aoi2", mock(Geometry.class));
    }

    @Test
    public void insertTest() {
        cache.insert("aoi1", aoiOne);
        cache.insert("aoi2", aoiTwo);
        assertEquals(cache.getAoi("aoi1"), aoiOne);
        assertEquals(cache.getAoi("aoi2"), aoiTwo);
    }

    @Test
    public void nameAlreadyExiststTest() {
        cache.insert("aoi1", aoiOne);
        assertThrows(IllegalArgumentException.class, () -> cache.insert("aoi1", aoiTwo));
    }

    @Test
    public void insertNullAoiTest() {
        assertThrows(IllegalArgumentException.class, () -> cache.insert("aoi1", null));
    }

    @Test
    public void insertNullNameTest() {
        assertThrows(IllegalArgumentException.class, () -> cache.insert(null, aoiOne));
    }

    @Test
    public void getAllAoisTest() {
        cache.insert("aoi1", aoiOne);
        cache.insert("aoi2", aoiTwo);

        Collection<AreaOfInterest> allAois = cache.getAllAois();
        assertTrue(allAois.contains(aoiOne));
        assertTrue(allAois.contains(aoiTwo));
    }
}
