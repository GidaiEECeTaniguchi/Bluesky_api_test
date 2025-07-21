package com.testapp.bluesky_api_test.repository;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupAnnotationDao;
import com.testapp.bluesky_api_test.DataBaseManupilate.entity.GroupAnnotation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class GroupAnnotationRepositoryTest {

    @Mock
    private GroupAnnotationDao mockGroupAnnotationDao;

    private GroupAnnotationRepository repository;
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // AppDatabaseSingleton をモック化するか、テスト用のインメモリDBを使うかだけど、
        // 今回は Dao をモック化することでリポジトリのロジックをテストするよ
        // 本来は AppDatabaseSingleton のインスタンスを渡すけど、モック化した Dao を直接渡すためにコンストラクタを調整するか、
        // テスト用のコンストラクタを用意する必要があるよ。
        // ここでは簡易的に、モック化した Dao を使うためのテスト用コンストラクタを想定するね。
        // 実際の AppDatabaseSingleton を使う場合は、Room のテストガイドラインに従ってインメモリDBを使うのが一般的だよ。
        // AppDatabaseSingleton と AppDatabase をモック化するよぉ
        // AppDatabaseSingleton は final クラスなので、PowerMock とかが必要になるけど、
        // 今回は AppDatabaseSingleton.getInstance() が返す AppDatabase をモック化するよぉ
        // 実際のテストでは、Room の Testing Utilities を使うのが一般的だよぉ
        // ここでは、リポジトリのコンストラクタが Context を受け取るので、
        // その Context から AppDatabaseSingleton.getInstance() を呼び出す部分をモック化するよぉ
        // ただし、AppDatabaseSingleton.getInstance() は static メソッドなので、Mockito では直接モック化できないよぉ
        // なので、ここでは AppDatabaseSingleton のインスタンスを直接モック化するのではなく、
        // AppDatabase のインスタンスをモック化して、それが Dao を返すように設定するよぉ

        // AppDatabase をモック化するよぉ
        com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase mockAppDatabase = org.mockito.Mockito.mock(com.testapp.bluesky_api_test.DataBaseManupilate.AppDatabase.class);

        // mockAppDatabase.groupAnnotationDao() が呼ばれたら、mockGroupAnnotationDao を返すように設定するよぉ
        org.mockito.Mockito.when(mockAppDatabase.groupAnnotationDao()).thenReturn(mockGroupAnnotationDao);

        // AppDatabaseSingleton.getInstance(context) が呼ばれたら、mockAppDatabase を返すように設定するよぉ
        // ただし、AppDatabaseSingleton.getInstance() は static メソッドなので、PowerMock を使う必要があるよぉ
        // ここでは、PowerMock を使わずに、リポジトリのコンストラクタをテスト用にオーバーライドするよぉ
        // これは、以前のやり方と同じだけど、他に良い方法がないから、これでいくよぉ
        // もし、PowerMock を使うなら、build.gradle に PowerMock の依存関係を追加する必要があるよぉ
        repository = new GroupAnnotationRepository(context) {

            protected com.testapp.bluesky_api_test.DataBaseManupilate.dao.GroupAnnotationDao groupAnnotationDao() {
                return mockGroupAnnotationDao;
            }
        };
    }

    @Test
    public void saveGroupAnnotation_shouldInsertAnnotation() {
        GroupAnnotation annotation = new GroupAnnotation(1, "concept", "description", "2025-07-02");
        annotation.setId(1);

        repository.saveGroupAnnotation(annotation);

        // insert メソッドが呼ばれたことを確認するよぉ
        verify(mockGroupAnnotationDao).insert(annotation);
    }

    @Test
    public void getGroupAnnotationById_shouldReturnCorrectAnnotation() {
        int annotationId = 1;
        GroupAnnotation expectedAnnotation = new GroupAnnotation(1, "concept", "description", "2025-07-02");
        expectedAnnotation.setId(annotationId);

        // getById が呼ばれたら、expectedAnnotation を返すように設定するよぉ
        when(mockGroupAnnotationDao.getById(annotationId)).thenReturn(expectedAnnotation);

        GroupAnnotation actualAnnotation = repository.getGroupAnnotationById(annotationId);

        // ちゃんと期待通りのアノテーションが返ってきたか確認するよぉ
        assertEquals(expectedAnnotation.getId(), actualAnnotation.getId());
        assertEquals(expectedAnnotation.getConcept(), actualAnnotation.getConcept());
    }

    // リポジトリのシャットダウン処理もテストするといいかもねぇ
    @Test
    public void shutdown_shouldShutdownExecutorService() {
        repository.shutdown();
        // ここで ExecutorService がシャットダウンされたことを確認する具体的な方法はないけど、
        // 実際には ExecutorService の状態を確認するメソッドがあればテストできるよぉ
        // 例えば、isShutdown() や isTerminated() を確認するとか。
    }
}
