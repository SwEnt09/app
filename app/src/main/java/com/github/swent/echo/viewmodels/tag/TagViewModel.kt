package com.github.swent.echo.viewmodels.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Stack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * This class represents the tags tree. This viewModel allows the composables get the tags from the
 * repository easily.
 *
 * @param repository a repository
 * @param rootTagId the id of the root tag of the tags tree
 */
@HiltViewModel(assistedFactory = TagViewModel.TagViewModelFactory::class)
class TagViewModel
@AssistedInject
constructor(private val repository: Repository, @Assisted private val rootTagId: String) :
    ViewModel() {
    private val _rootTag =
        MutableStateFlow<Tag>(
            Tag("1d253a7e-eb8c-4546-bc98-1d3adadcffe8", "ROOT TAG: DO NOT DELETE")
        )
    val rootTag = _rootTag.asStateFlow()
    private val _tags = MutableStateFlow<List<Tag>>(listOf())
    val tags = _tags.asStateFlow()
    private var _currentDepth = MutableStateFlow(0)
    val currentDepth = _currentDepth.asStateFlow()
    private var _tagParents = MutableStateFlow(Stack<Tag>())
    val tagParents = _tagParents.asStateFlow()
    private val _subTagsMap = MutableStateFlow(mapOf(Pair(rootTag.value, setOf<Tag>())))
    val subTagsMap = _subTagsMap.asStateFlow()

    init {
        initialize()
    }

    /** Initialize the variables exposed by the viewmodel. */
    private fun initialize() {
        viewModelScope.launch {
            _rootTag.value = repository.getTag(rootTagId) ?: _rootTag.value
            _tags.value = repository.getSubTags(rootTag.value.tagId)
            _subTagsMap.value += Pair(rootTag.value, tags.value.toSet())
            _tagParents.value.push(rootTag.value)
            prefetchTags(tags.value)
        }
    }

    /**
     * Fetch the sub-tags of the tag list before they are displayed.
     *
     * @param tagList the list of tags to prefetch sub-tags from
     */
    private fun prefetchTags(tagList: List<Tag>) {
        viewModelScope.launch {
            for (tag in tagList) {
                _subTagsMap.value += Pair(tag, repository.getSubTags(tag.tagId).toSet())
            }
        }
    }

    /** Goes up in the tag tree. */
    fun goUp() {
        viewModelScope.launch {
            if (_currentDepth.value > 0) {
                _currentDepth.value--
                _tagParents.value.pop()
                val subTags = subTagsMap.value[tagParents.value.peek()]?.toList()
                _tags.value =
                    if (subTags.isNullOrEmpty()) {
                        repository.getSubTags(_tagParents.value.peek().tagId)
                    } else {
                        subTags
                    }
                _subTagsMap.value += Pair(tagParents.value.peek(), tags.value.toSet())
                prefetchTags(tags.value)
            }
        }
    }

    /**
     * Goes down in the tag tree.
     *
     * @param tag the new parent tag
     */
    fun goDown(tag: Tag) {
        viewModelScope.launch {
            var subTags = subTagsMap.value[tag]?.toList()
            if (subTags.isNullOrEmpty()) {
                subTags = repository.getSubTags(tag.tagId)
            }
            if (subTags.isNotEmpty()) {
                _tags.value = subTags
                _currentDepth.value++
                _tagParents.value.push(tag)
                _subTagsMap.value += Pair(tag, tags.value.toSet())
                prefetchTags(tags.value)
            }
        }
    }

    /**
     * Return the tag associated to a tagId.
     *
     * @param tagId a tag id
     */
    fun getTag(tagId: String): StateFlow<Tag> {
        val tag = MutableStateFlow<Tag>(Tag("", ""))
        viewModelScope.launch { tag.value = repository.getTag(tagId) ?: Tag("", "") }
        return tag
    }

    /** Reset the tag tree. */
    fun reset() {
        _currentDepth.value = 0
        _tagParents.value.clear()
        initialize()
    }

    /** Factory for the TagViewModel class. Allow to inject the id of the root tag. */
    @AssistedFactory
    interface TagViewModelFactory {

        /**
         * Create a new TagViewModel.
         *
         * @param rootTagId the id of the root tag
         */
        fun create(rootTagId: String = ""): TagViewModel
    }
}
