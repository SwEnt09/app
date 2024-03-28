package com.github.swent.echo.data.repository

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.User
import com.github.swent.echo.data.model.UserProfile

interface IRepository {
    fun getAssociation(): Association

    fun setAssociation(association: Association)

    fun getEvent(): Event

    fun setEvent(event: Event)

    fun getEventList(): List<Event>

    fun getTag(): Tag

    fun setTag(tag: Tag)

    fun getTagList(): List<Tag>

    fun getUser(): User

    fun setUser(user: User)

    fun getUserProfile(): UserProfile

    fun setUserProfile(userProfile: UserProfile)
}
