package com.cookiss.di

import com.cookiss.data.repository.follow.FollowRepository
import com.cookiss.data.repository.follow.FollowRepositoryImpl
import com.cookiss.data.repository.user.UserRepository
import com.cookiss.data.repository.user.UserRepositoryImpl
import com.cookiss.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }

    single<UserRepository> {
        UserRepositoryImpl(get())
    }

    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }
}