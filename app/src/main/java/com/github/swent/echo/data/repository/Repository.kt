package com.github.swent.echo.data.repository

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.User
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSource

class Repository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : IRepository {
    override fun getAssociation(): Association {
        TODO("Not yet implemented")
        // Fetch data from remote, if fails, fetch from local
    }

    override fun setAssociation(association: Association) {
        TODO("Not yet implemented")
    }

    override fun getEvent(): Event {
        TODO("Not yet implemented")
    }

    override fun setEvent(event: Event) {
        TODO("Not yet implemented")
    }

    override fun getEventList(): List<Event> {
        TODO("Not yet implemented")
    }

    override fun getTag(): Tag {
        TODO("Not yet implemented")
    }

    override fun setTag(tag: Tag) {
        TODO("Not yet implemented")
    }

    override fun getTagList(): List<Tag> {
        TODO("Not yet implemented")
    }

    override fun getUser(): User {
        TODO("Not yet implemented")
    }

    override fun setUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun getUserProfile(): UserProfile {
        TODO("Not yet implemented")
    }

    override fun setUserProfile(userProfile: UserProfile) {
        TODO("Not yet implemented")
    }
}
