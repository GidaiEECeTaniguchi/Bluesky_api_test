package com.testapp.bluesky_api_test.repository;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupRefDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRef;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class GroupRefRepositoryTest {

    @Mock
    private GroupRefDao mockGroupRefDao;

    private GroupRefRepository repository;
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        repository = new GroupRefRepository(context) {

            protected GroupRefDao getGroupRefDao() {
                return mockGroupRefDao;
            }
        };
    }

    @Test
    public void saveGroupRef_shouldInsertRef() {
        GroupRef ref = new GroupRef(1, "title", "type", "path");
        ref.setId(1);

        repository.saveGroupRef(ref);

        verify(mockGroupRefDao).insert(ref);
    }

    @Test
    public void getGroupRefById_shouldReturnCorrectRef() {
        int refId = 1;
        GroupRef expectedRef = new GroupRef(1, "title", "type", "path");
        expectedRef.setId(refId);

        when(mockGroupRefDao.getById(refId)).thenReturn(expectedRef);

        GroupRef actualRef = repository.getGroupRefById(refId);

        assertEquals(expectedRef.getId(), actualRef.getId());
        assertEquals(expectedRef.getTitle(), actualRef.getTitle());
    }

    @Test
    public void shutdown_shouldShutdownExecutorService() {
        repository.shutdown();
    }
}
