package com.example.youtube.di

import android.content.Context
import com.example.youtube.data.api.YouTubeApiClient
import com.example.youtube.data.repository.VideoRepository
import com.example.youtube.data.room.AppDatabase
import com.example.youtube.data.room.favorite.FavoriteVideoDao
import com.example.youtube.data.room.searchvideo.SearchHistoryDao
import com.example.youtube.data.room.watchedvideo.WatchedVideoDao
import com.example.youtube.domain.usecase.AddFavoriteVideoUseCase
import com.example.youtube.domain.usecase.AddToWatchedVideosUseCase
import com.example.youtube.domain.usecase.AddToWatchedVideosUseCaseImpl
import com.example.youtube.domain.usecase.DeleteSearchHistoryUseCase
import com.example.youtube.domain.usecase.GetFavoriteVideosUseCase
import com.example.youtube.domain.usecase.LoadSearchHistoryUseCase
import com.example.youtube.domain.usecase.RecommendVideosUseCase
import com.example.youtube.domain.usecase.RemoveFavoriteVideoUseCase
import com.example.youtube.domain.usecase.SearchVideosUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideYouTubeApiClient(): YouTubeApiClient {
        val apiKey = "AIzaSyBq_xLtb_RS3JtLeVFS9myQYJCAuDaDepc"
        return YouTubeApiClient(apiKey)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideWatchedVideoDao(appDatabase: AppDatabase): WatchedVideoDao {
        return appDatabase.watchedVideoDao()
    }

    @Provides
    @Singleton
    fun provideVideoRepository(
        apiClient: YouTubeApiClient,
        watchedVideoDao: WatchedVideoDao,
        searchHistoryDao: SearchHistoryDao,
        favoriteVideoDao: FavoriteVideoDao,
    ): VideoRepository {
        return VideoRepository(apiClient, watchedVideoDao, searchHistoryDao, favoriteVideoDao)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(appDatabase: AppDatabase): SearchHistoryDao {
        return appDatabase.searchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideLoadSearchHistoryUseCase(repository: VideoRepository): LoadSearchHistoryUseCase {
        return LoadSearchHistoryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteSearchHistoryUseCase(repository: VideoRepository): DeleteSearchHistoryUseCase {
        return DeleteSearchHistoryUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchVideosUseCase(repository: VideoRepository): SearchVideosUseCase {
        return SearchVideosUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddFavoriteVideoUseCase(
        videoRepository: VideoRepository
    ): AddFavoriteVideoUseCase {
        return AddFavoriteVideoUseCase(videoRepository)
    }

    @Provides
    @Singleton
    fun provideRemoveFavoriteVideoUseCase(
        videoRepository: VideoRepository
    ): RemoveFavoriteVideoUseCase {
        return RemoveFavoriteVideoUseCase(videoRepository)
    }

    @Provides
    @Singleton
    fun provideGetFavoriteVideoUseCase(
        videoRepository: VideoRepository
    ): GetFavoriteVideosUseCase {
        return GetFavoriteVideosUseCase(videoRepository)
    }

    @Provides
    @Singleton
    fun provideFavoriteVideoDao(appDatabase: AppDatabase): FavoriteVideoDao {
        return appDatabase.favoriteVideoDao()
    }

    @Provides
    @Singleton
    fun provideRecommendVideosUseCase(repository: VideoRepository): RecommendVideosUseCase {
        return RecommendVideosUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddToWatchedVideosUseCase(repository: VideoRepository): AddToWatchedVideosUseCase {
        return AddToWatchedVideosUseCaseImpl(repository)
    }
}