package com.testapp.bluesky_api_test.repository;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupRewriteDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupRewrite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class GroupRewriteRepositoryTest {

    @Mock
    private GroupRewriteDao mockGroupRewriteDao;

    private GroupRewriteRepository repository;
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        repository = new GroupRewriteRepository(context) {

            protected GroupRewriteDao getGroupRewriteDao() {
                return mockGroupRewriteDao;
            }
        };
    }

    @Test
    public void saveGroupRewrite_shouldInsertRewrite() {
        GroupRewrite rewrite = new GroupRewrite(1, "matcher", "rewritten", "description", "2025-07-02");
        rewrite.setId(1);

        repository.saveGroupRewrite(rewrite);

        verify(mockGroupRewriteDao).insert(rewrite);
    }

    @Test
    public void getGroupRewriteById_shouldReturnCorrectRewrite() {
        int rewriteId = 1;
        GroupRewrite expectedRewrite = new GroupRewrite(1, "matcher", "rewritten", "description", "2025-07-02");
        expectedRewrite.setId(rewriteId);

        when(mockGroupRewriteDao.getById(rewriteId)).thenReturn(expectedRewrite);

        GroupRewrite actualRewrite = repository.getGroupRewriteById(rewriteId);

        assertEquals(expectedRewrite.getId(), actualRewrite.getId());
        assertEquals(expectedRewrite.getMatcher_text(), actualRewrite.getMatcher_text());
    }

    @Test
    public void shutdown_shouldShutdownExecutorService() {
        repository.shutdown();
    }
}
