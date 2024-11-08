package com.djatar.ardath.injection

import android.content.Context
import com.djatar.ardath.feature.data.repository.ChatRepositoryImpl
import com.djatar.ardath.feature.data.repository.UserRepositoryImpl
import com.djatar.ardath.feature.domain.repository.ChatRepository
import com.djatar.ardath.feature.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
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
    fun provideDatabase() : FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    fun provideAuth() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideMessaging() : FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        @ApplicationContext context: Context,
        database: FirebaseDatabase,
        auth: FirebaseAuth,
        messaging: FirebaseMessaging
    ) : ChatRepository {
        return ChatRepositoryImpl(context, database, auth, messaging)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        database: FirebaseDatabase,
        auth: FirebaseAuth
    ) : UserRepository {
        return UserRepositoryImpl(database, auth)
    }
}