package com.promecarus.storyapp.utils

//@ExperimentalPagingApi
//@RunWith(AndroidJUnit4::class)
//class StoryRemoteMediatorTest {
//    @Mock
//    private lateinit var apiService: ApiService
//
//    @Mock
//    private lateinit var sessionPreference: SessionPreference
//
//    @Mock
//    private lateinit var settingPreference: SettingPreference
//
//    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
//        ApplicationProvider.getApplicationContext(), StoryDatabase::class.java
//    ).allowMainThreadQueries().build()
//
//    @Test
//    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
//        val remoteMediator =
//            StoryRemoteMediator(apiService, sessionPreference, settingPreference, mockDb)
//
//        val pagingState = PagingState<Int, Story>(listOf(), null, PagingConfig(10), 10)
//        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
//        assertTrue(result is RemoteMediator.MediatorResult.Success)
//        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
//    }
//
//    @After
//    fun tearDown() {
//        mockDb.clearAllTables()
//    }
//}
